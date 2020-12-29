package com.orvillex.bortus.manager.config;

import com.orvillex.bortus.manager.config.security.LoginProperties;
import com.orvillex.bortus.manager.config.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 登录与JWT配置
 * @author y-z-f
 * @version 0.1
 */
@Configuration
public class SecurityConfig {
    @Bean
    @ConfigurationProperties(prefix = "login", ignoreUnknownFields = true)
    public LoginProperties loginProperties() {
        return new LoginProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = true)
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }
}
