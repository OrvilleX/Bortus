package com.orvillex.bortus.manager.config.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 任务调度配置
 * @author y-z-f
 * @version 0.1
 */
@Configuration
public class SchedulerConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "schduler", ignoreUnknownFields = true)
    public SchedulerProperties schedulerProperties() {
        return new SchedulerProperties();
    }
}
