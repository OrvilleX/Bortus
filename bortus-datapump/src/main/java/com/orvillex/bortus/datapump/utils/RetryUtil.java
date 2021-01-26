package com.orvillex.bortus.datapump.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.job.log.JobLogger;

public final class RetryUtil {
    private static final long MAX_SLEEP_MILLISECOND = 256 * 1000;

    public static <T> T executeWithRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond,
            boolean exponential) throws Exception {
        Retry retry = new Retry();
        return retry.doRetry(callable, retryTimes, sleepTimeInMilliSecond, exponential, null);
    }

    /**
     * 重试次数工具方法.
     *
     * @param callable               实际逻辑
     * @param retryTimes             最大重试次数（>1）
     * @param sleepTimeInMilliSecond 运行失败后休眠对应时间再重试
     * @param exponential            休眠时间是否指数递增
     * @param <T>                    返回值类型
     * @param retryExceptionClasss   出现指定的异常类型时才进行重试
     * @return 经过重试的callable的执行结果
     */
    public static <T> T executeWithRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond,
            boolean exponential, List<Class<?>> retryExceptionClasss) throws Exception {
        Retry retry = new Retry();
        return retry.doRetry(callable, retryTimes, sleepTimeInMilliSecond, exponential, retryExceptionClasss);
    }

    public static <T> T asyncExecuteWithRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond,
            boolean exponential, long timeoutMs, ThreadPoolExecutor executor) throws Exception {
        Retry retry = new AsyncRetry(timeoutMs, executor);
        return retry.doRetry(callable, retryTimes, sleepTimeInMilliSecond, exponential, null);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, 5, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    private static class Retry {
        public <T> T doRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond, boolean exponential,
                List<Class<?>> retryExceptionClasss) throws Exception {
            if (null == callable) {
                throw new IllegalArgumentException("系统编程错误, 入参callable不能为空 ! ");
            }
            if (retryTimes < 1) {
                throw new IllegalArgumentException(String.format("系统编程错误, 入参retrytime[%d]不能小于1 !", retryTimes));
            }
            Exception saveException = null;
            for (int i = 0; i < retryTimes; i++) {
                try {
                    return call(callable);
                } catch (Exception e) {
                    saveException = e;
                    if (i == 0) {
                        JobLogger.log(
                                String.format("Exception when calling callable, 异常Msg:%s", saveException.getMessage()));
                        JobLogger.log(saveException);
                    }
                    if (null != retryExceptionClasss && !retryExceptionClasss.isEmpty()) {
                        boolean needRetry = false;
                        for (Class<?> eachExceptionClass : retryExceptionClasss) {
                            if (eachExceptionClass == e.getClass()) {
                                needRetry = true;
                                break;
                            }
                        }
                        if (!needRetry) {
                            throw saveException;
                        }
                    }
                    if (i + 1 < retryTimes && sleepTimeInMilliSecond > 0) {
                        long startTime = System.currentTimeMillis();
                        long timeToSleep;
                        if (exponential) {
                            timeToSleep = sleepTimeInMilliSecond * (long) Math.pow(2, i);
                            if (timeToSleep >= MAX_SLEEP_MILLISECOND) {
                                timeToSleep = MAX_SLEEP_MILLISECOND;
                            }
                        } else {
                            timeToSleep = sleepTimeInMilliSecond;
                            if (timeToSleep >= MAX_SLEEP_MILLISECOND) {
                                timeToSleep = MAX_SLEEP_MILLISECOND;
                            }
                        }
                        try {
                            Thread.sleep(timeToSleep);
                        } catch (InterruptedException ignored) {
                        }
                        long realTimeSleep = System.currentTimeMillis() - startTime;
                        JobLogger.log(String.format(
                                "Exception when calling callable, 即将尝试执行第%s次重试.本次重试计划等待[%s]ms,实际等待[%s]ms, 异常Msg:[%s]",
                                i + 1, timeToSleep, realTimeSleep, e.getMessage()));
                    }
                }
            }
            throw saveException;
        }

        protected <T> T call(Callable<T> callable) throws Exception {
            return callable.call();
        }
    }

    private static class AsyncRetry extends Retry {
        private long timeoutMs;
        private ThreadPoolExecutor executor;

        public AsyncRetry(long timeoutMs, ThreadPoolExecutor executor) {
            this.timeoutMs = timeoutMs;
            this.executor = executor;
        }

        @Override
        protected <T> T call(Callable<T> callable) throws Exception {
            Future<T> future = executor.submit(callable);
            try {
                return future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                JobLogger.log("Try once failed");
                JobLogger.log(e);
                throw e;
            } finally {
                if (!future.isDone()) {
                    future.cancel(true);
                    JobLogger.log("Try once task not done, cancel it, active count: " + executor.getActiveCount());
                }
            }
        }
    }
}
