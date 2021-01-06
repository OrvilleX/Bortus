package com.orvillex.bortus.manager.exception;

/**
 * 调度异常
 */
public class SchedulerException extends RuntimeException {
    private static final long serialVersionUID = 5560941377722406015L;

    public SchedulerException() {
    }

    public SchedulerException(String message) {
        super(message);
    }
}
