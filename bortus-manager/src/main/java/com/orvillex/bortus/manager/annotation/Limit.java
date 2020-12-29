package com.orvillex.bortus.manager.annotation;

import com.orvillex.bortus.manager.aop.LimitType;
import java.lang.annotation.*;

/**
 * 用于限流的注解
 * @author y-z-f
 * @version 0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    /**
     * 资源名称
     */
    String name() default "";

    /**
     * 资源标识
     */
    String key() default "";
    
    /**
     * 标识前缀
     */
    String prefix() default "";

    /**
     * 时间范围，单位秒
     */
    int period();

    /**
     * 限制访问次数
     */
    int count();

    /**
     * 限制类型
     */
    LimitType limitType() default LimitType.CUSTOMER;
}
