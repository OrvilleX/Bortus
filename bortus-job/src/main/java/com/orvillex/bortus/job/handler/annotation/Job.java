package com.orvillex.bortus.job.handler.annotation;

import java.lang.annotation.*;

/**
 * 任务处理程序
 * 
 * @author y-z-f
 * @version 0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Job {

    /**
     * 名字
     */
    String value();

    /**
     * 初始化
     */
    String init() default "";

    /**
     * 销毁
     */
    String destroy() default "";
}
