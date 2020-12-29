package com.orvillex.utils;

import com.orvillex.config.thread.AsyncTaskProperties;
import com.orvillex.config.thread.ThreadFactoryName;
import com.orvillex.handler.SpringContextHolder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 提供线程池工具类
 * @author y-z-f
 * @version 0.1
 */
public class ThreadPoolExecutorUtil {

    public static ThreadPoolExecutor getPoll() {
        AsyncTaskProperties properties = SpringContextHolder.getBean(AsyncTaskProperties.class);
        return new ThreadPoolExecutor(
          properties.getCorePoolSize(),
          properties.getMaxPoolSize(),
          properties.getKeepAliveSeconds(),
          TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(properties.getQueueCapacity()),
          new ThreadFactoryName()
        );
    }
}
