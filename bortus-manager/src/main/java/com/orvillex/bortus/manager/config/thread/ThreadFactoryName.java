package com.orvillex.bortus.manager.config.thread;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程创建工厂
 * 主要用于自定义线程池和线程名称
 * @author y-z-f
 * @version 0.1
 */
@Component
public class ThreadFactoryName implements ThreadFactory {
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ThreadFactoryName() {
        this("x-pool");
    }

    public ThreadFactoryName(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = name + poolNumber.getAndIncrement();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + "-thread-" + threadNumber.getAndIncrement(),0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
