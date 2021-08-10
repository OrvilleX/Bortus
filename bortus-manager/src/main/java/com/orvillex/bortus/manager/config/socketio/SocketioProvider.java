package com.orvillex.bortus.manager.config.socketio;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 提供SocketIO服务原生接口
 * @author y-z-f
 * @version 0.1
 */
@Component
public class SocketioProvider {
    private SocketioProperties properties;

    public SocketioProvider(SocketioProperties properties) {
        this.properties = properties;
    }
    
    @Bean
    public SocketIOServer createSocketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(properties.getHost());
        config.setPort(properties.getPort());
        config.setBossThreads(properties.getBossCount());
        config.setWorkerThreads(properties.getWorkCount());
        config.setAllowCustomRequests(properties.isAllowCustomRequests());
        config.setUpgradeTimeout(properties.getUpgradeTimeout());
        config.setPingTimeout(properties.getPingTimeout());
        config.setPingInterval(properties.getPingInterval());
        config.setMaxFramePayloadLength(properties.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(properties.getMaxHttpContentLength());
        //config.setStoreFactory(clientStoreFactory);
        return new SocketIOServer(config);
    }
}
