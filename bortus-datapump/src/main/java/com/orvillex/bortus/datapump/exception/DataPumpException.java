package com.orvillex.bortus.datapump.exception;

public class DataPumpException extends RuntimeException {
    private static final long serialVersionUID = -7181516178615087879L;

    public DataPumpException(String message) {
        super(message);
    }

    public DataPumpException(String message, Throwable cause) {
        super(message, cause);
    }
}
