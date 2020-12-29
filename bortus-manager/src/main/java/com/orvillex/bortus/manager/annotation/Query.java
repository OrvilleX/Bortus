package com.orvillex.bortus.manager.annotation;

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
     * 连接查询的属性名
     */
    String joinName() default "";

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
          EQUAL,
          /**
           * 大于等于
           */
          GREATER_THAN,
          /**
           * 小于等于
           */
          LESS_THAN,
          /**
           * 中模糊查询
           */
          INNER_LIKE,
          /**
           * 左模糊查询
           */
          LEFT_LIKE,
          /**
           * 右模糊查询
           */
          RIGHT_LIKE,
          /**
           * 小于
           */
          LESS_THAN_NQ,
          /**
           * 包含
           */
          IN,
          /**
           * 不等于
           */
          NOT_EQUAL,
          /**
           * 区间
           */
          BETWEEN,
          /**
           * 不为空
           */
          NOT_NULL,
          /**
           * 为空
           */
          IS_NULL
    }

    enum Join {
         LEFT, RIGHT, INNER
    }
}
