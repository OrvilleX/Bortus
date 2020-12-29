package com.orvillex.bortus.manager.annotation;

import com.orvillex.bortus.manager.enums.LogActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解属性
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    
    String value() default "";
    boolean enable() default true;
    LogActionType type() default LogActionType.SELECT;
}
