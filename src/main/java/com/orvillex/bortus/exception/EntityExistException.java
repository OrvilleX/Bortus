package com.orvillex.bortus.exception;

import org.springframework.util.StringUtils;


/**
 * 实体已存在异常
 * @author y-z-f
 * @version 0.1
 */
public class EntityExistException extends RuntimeException {

    private static final long serialVersionUID = -7016750488091536956L;

    public EntityExistException(Class<?> clazz, String field, String val) {
        super(EntityExistException.generateMessage(clazz.getSimpleName(), field, val));
    }
    
    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity) + " with " + field + " " + val + " existed";
    }
}
