package com.orvillex.bortus.manager.config.wx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 微信小程序SDK
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    private String appId;
    private String appSecret;
}
