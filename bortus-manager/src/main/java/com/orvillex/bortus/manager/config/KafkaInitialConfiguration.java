package com.orvillex.bortus.manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka主题配置
 */
@Configuration
public class KafkaInitialConfiguration {
    
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("testtopi", 2, (short)2);
    }
}
