package com.orvillex.bortus.datapump.core.config;

import com.orvillex.bortus.job.executor.JobSpringExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {
    private Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value("${bortus.job.admin.addresses}")
    private String adminAddresses;

    @Value("${bortus.job.accessToken}")
    private String accessToken;

    @Value("${bortus.job.executor.appname}")
    private String appname;

    @Value("${bortus.job.executor.address}")
    private String address;

    @Value("${bortus.job.executor.ip}")
    private String ip;

    @Value("${bortus.job.executor.port}")
    private int port;

    @Value("${bortus.job.executor.logpath}")
    private String logPath;

    @Value("${bortus.job.executor.logretentiondays}")
    private int logRetentionDays;


    @Bean
    public JobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> bortus-job config init.");
        JobSpringExecutor jobSpringExecutor = new JobSpringExecutor();
        jobSpringExecutor.setAdminAddresses(adminAddresses);
        jobSpringExecutor.setAppname(appname);
        jobSpringExecutor.setAddress(address);
        jobSpringExecutor.setIp(ip);
        jobSpringExecutor.setPort(port);
        jobSpringExecutor.setAccessToken(accessToken);
        jobSpringExecutor.setLogPath(logPath);
        jobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return jobSpringExecutor;
    }
}
