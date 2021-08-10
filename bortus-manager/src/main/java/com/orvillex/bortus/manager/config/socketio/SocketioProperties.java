package com.orvillex.bortus.manager.config.socketio;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "socketio")
public class SocketioProperties {
    private String host;
    private Integer port;
    private int maxFramePayloadLength;
    private int maxHttpContentLength;
    private int bossCount;
    private int workCount;
    private boolean allowCustomRequests;
    private int upgradeTimeout;
    private int pingTimeout;
    private int pingInterval;
}
