package com.orvillex.bortus.datapump.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置项
 */
@Configuration
public class DataPumpConfig {
    @Bean
    @ConfigurationProperties(prefix = "datapump.column", ignoreUnknownFields = true)
    public ColumnProperties columnProperties() {
        return new ColumnProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "datapump.task", ignoreUnknownFields = true)
    public TaskProperties taskProperties() {
        return new TaskProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "datapump.transport", ignoreUnknownFields = true)
    public TransportProperties transportProperties() {
        return new TransportProperties();
    }
}
