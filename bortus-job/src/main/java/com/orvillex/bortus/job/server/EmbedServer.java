package com.orvillex.bortus.job.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.job.biz.DefaultExecutorBiz;
import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.models.*;
import com.orvillex.bortus.job.thread.ExecutorRegistryThread;
import com.orvillex.bortus.job.util.JobRemotingUtil;
import com.orvillex.bortus.job.util.ThrowableUtil;

public class EmbedServer {
    private static final Logger logger = LoggerFactory.getLogger(EmbedServer.class);

    private ExecutorBiz executorBiz;
    private Thread thread;

    public void start(final String address, final int port, final String appname, final String accessToken) {
        executorBiz = new DefaultExecutorBiz();
        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                ThreadPoolExecutor bizThreadPool = new ThreadPoolExecutor(
                        0,
                        200,
                        60L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(2000),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "xxl-rpc, EmbedServer bizThreadPool-" + r.hashCode());
                            }
                        },
                        new RejectedExecutionHandler() {
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                throw new RuntimeException("xxl-job, EmbedServer bizThreadPool is EXHAUSTED!");
                            }
                        });


                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    channel.pipeline()
                                            .addLast(new IdleStateHandler(0, 0, 30 * 3, TimeUnit.SECONDS))
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                            .addLast(new EmbedHttpServerHandler(executorBiz, accessToken, bizThreadPool));
                                }
                            })
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    ChannelFuture future = bootstrap.bind(port).sync();

                    logger.info(">>>>>>>>>>> xxl-job remoting server start success, nettype = {}, port = {}", EmbedServer.class, port);

                    startRegistry(appname, address);
                    future.channel().closeFuture().sync();

                } catch (InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        logger.info(">>>>>>>>>>> xxl-job remoting server stop.");
                    } else {
                        logger.error(">>>>>>>>>>> xxl-job remoting server error.", e);
                    }
                } finally {
                    try {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() throws Exception {
        if (thread!=null && thread.isAlive()) {
            thread.interrupt();
        }

        stopRegistry();
        logger.info(">>>>>>>>>>> xxl-job remoting server destroy success.");
    }

    // ---------------------- registry ----------------------

    /**
     * netty_http
     */
    public static class EmbedHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private static final Logger logger = LoggerFactory.getLogger(EmbedHttpServerHandler.class);

        private ExecutorBiz executorBiz;
        private String accessToken;
        private ThreadPoolExecutor bizThreadPool;
        public EmbedHttpServerHandler(ExecutorBiz executorBiz, String accessToken, ThreadPoolExecutor bizThreadPool) {
            this.executorBiz = executorBiz;
            this.accessToken = accessToken;
            this.bizThreadPool = bizThreadPool;
        }

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

            String requestData = msg.content().toString(CharsetUtil.UTF_8);
            String uri = msg.uri();
            HttpMethod httpMethod = msg.method();
            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            String accessTokenReq = msg.headers().get(JobRemotingUtil.BORTUS_JOB_ACCESS_TOKEN);

            bizThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Object responseObj = process(httpMethod, uri, requestData, accessTokenReq);
                    String responseJson = JSON.toJSONString(responseObj);
                    writeResponse(ctx, keepAlive, responseJson);
                }
            });
        }

        private Object process(HttpMethod httpMethod, String uri, String requestData, String accessTokenReq) {
            if (HttpMethod.POST != httpMethod) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
            }
            if (uri==null || uri.trim().length()==0) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
            }
            if (accessToken!=null
                    && accessToken.trim().length()>0
                    && !accessToken.equals(accessTokenReq)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
            }

            try {
                if ("/beat".equals(uri)) {
                    return executorBiz.beat();
                } else if ("/idleBeat".equals(uri)) {
                    IdleBeatParam idleBeatParam = JSON.parseObject(requestData, IdleBeatParam.class);
                    return executorBiz.idleBeat(idleBeatParam);
                } else if ("/run".equals(uri)) {
                    TriggerParam triggerParam = JSON.parseObject(requestData, TriggerParam.class);
                    return executorBiz.run(triggerParam);
                } else if ("/kill".equals(uri)) {
                    KillParam killParam = JSON.parseObject(requestData, KillParam.class);
                    return executorBiz.kill(killParam);
                } else if ("/log".equals(uri)) {
                    LogParam logParam = JSON.parseObject(requestData, LogParam.class);
                    return executorBiz.log(logParam);
                } else {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "request error:" + ThrowableUtil.toString(e));
            }
        }

        /**
         * write response
         */
        private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, String responseJson) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.writeAndFlush(response);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error(">>>>>>>>>>> xxl-job provider netty_http server caught exception", cause);
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.channel().close();
                logger.debug(">>>>>>>>>>> xxl-job provider netty_http server close an idle channel.");
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    public void startRegistry(final String appname, final String address) {
        ExecutorRegistryThread.getInstance().start(appname, address);
    }

    public void stopRegistry() {
        ExecutorRegistryThread.getInstance().toStop();
    }
}
