package com.orvillex.bortus.manager.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置类
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
@Configuration
public class AsyncTaskExecutePool implements AsyncConfigurer {
    private final AsyncTaskProperties config;

    public  AsyncTaskExecutePool(AsyncTaskProperties config) {
        this.config = config;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize()); // 核心线程池大小
        executor.setMaxPoolSize(config.getMaxPoolSize()); // 最大线程数
        executor.setQueueCapacity(config.getQueueCapacity()); // 队列容量
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds()); // 活跃时间
        executor.setThreadNamePrefix("x-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 当前线程达到上限后新任务将在调用者所在线程执行
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("====" + throwable.getMessage() + "====", throwable);
            log.error("exception method:" + method.getName());
        };
    }
}
