package com.orvillex.bortus.manager.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.orvillex.bortus.manager.service.SocketioService;
import com.orvillex.bortus.manager.service.dto.PushMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service(value = "socketioservice")
public class SocketioServiceImpl implements SocketioService {
    private SocketIOServer socketIOServer;
    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();

    public SocketioServiceImpl(SocketIOServer server) {
        this.socketIOServer = server;
    }

    @PostConstruct
    private void autoStartup() throws Exception {
        start();
    }

    @PreDestroy
    private void autoStop() throws Exception  {
        stop();
    }

    @Override
    public void start() throws Exception {
        // 监听客户端连接
        socketIOServer.addConnectListener(client -> {

        });

        // 监听客户端断开连接
        socketIOServer.addDisconnectListener(client -> {

        });

        // 处理自定义的事件，与连接监听类似
        socketIOServer.addEventListener(PUSH_EVENT, PushMessage.class, (client, data, ackSender) -> {
            // TODO do something
        });
        socketIOServer.start();
    }

    @Override
    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }

    @Override
    public void pushMessageToUser(PushMessage pushMessage) {
        String userid = pushMessage.getUserId();
        if (StringUtils.isNotBlank(userid)) {
            SocketIOClient client = clientMap.get(userid);
            if (client != null)
                client.sendEvent(PUSH_EVENT, pushMessage);
        }
    }
}
