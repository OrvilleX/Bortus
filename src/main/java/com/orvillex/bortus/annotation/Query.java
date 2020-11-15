package com.orvillex.bortus.annotation;

import java.lang.annotation.*;

/**
 * 查询字段注解
 * @author y-z-f
 * @version 0.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    
    /**
     * 字段名
     */
    String propName() default "";

    /**
     * 查询方式
     */
    Type type() default Type.EQUAL;

    /**
     * 连接类型
     */
    Join join() default Join.LEFT;

    /**
     * 多字段模糊查询，多个字段采用逗号隔开
     */
    String blurry() default "";

    enum Type {
         /**
          * 等于
          */
         EQUAL
    }

    enum Join {
         LEFT, RIGHT, INNER
    }
}
