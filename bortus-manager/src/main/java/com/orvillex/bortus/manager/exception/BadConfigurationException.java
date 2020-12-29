package com.orvillex.bortus.manager.exception;

/**
 * 配置异常
 * @author y-z-f
 * @version 0.1
 */
public class BadConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 5660176656240524097L;
    
    public BadConfigurationException() {
        super();
    }

    public BadConfigurationException(String message) {
        super(message);
    }

    public BadConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConfigurationException(Throwable cause) {
        super(cause);
    }

    protected BadConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
