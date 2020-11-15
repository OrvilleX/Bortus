package com.orvillex.bortus.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 错误请求异常
 */
@Getter
public class BadRequestException extends RuntimeException {
    
    private static final long serialVersionUID = 2872856990382284416L;
    private Integer status = BAD_REQUEST.value();

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status.value();
    }

}
