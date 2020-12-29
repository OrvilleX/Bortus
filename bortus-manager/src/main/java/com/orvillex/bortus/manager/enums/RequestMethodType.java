package com.orvillex.bortus.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求方式类型
 * @author y-z-f
 * @version 0.1
 */
@Getter
@AllArgsConstructor
public enum RequestMethodType {
    
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    ALL("ALL");

    private final String type;

    public static RequestMethodType find(String type) {
        for (RequestMethodType value : RequestMethodType.values()) {
            if (type.equals(value.getType())) {
                return value;
            }
        }
        return ALL;
    }
}
