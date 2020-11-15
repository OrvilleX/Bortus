package com.orvillex.bortus.exception;

import org.springframework.util.StringUtils;

/**
 * 实体不存在异常
 * @author y-z-f
 * @version 0.1
 */
public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1518167838594678977L;

    public EntityNotFoundException(Class clazz, String field, String val) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), field, val));
    }
    
    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity) + " with " + field + " " + val + " does not exist";
    }
}
