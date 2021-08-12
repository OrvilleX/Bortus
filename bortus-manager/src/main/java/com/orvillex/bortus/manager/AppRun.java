package com.orvillex.bortus.manager;

import com.orvillex.bortus.manager.annotation.AnonymousGetMapping;
import com.orvillex.bortus.manager.handler.SpringContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

import co.elastic.apm.attach.ElasticApmAttacher;

/**
 * 主入口
 * @author y-z-f
 * @version 0.1
 */
@EnableAsync
@RestController
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AppRun {

    public static void main(String[] args) {
        ElasticApmAttacher.attach();
        SpringApplication.run(AppRun.class, args);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
        fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return fa;
    }

    @AnonymousGetMapping("/")
    public String index() {
        return "Backend service started successfully";
    }
}
