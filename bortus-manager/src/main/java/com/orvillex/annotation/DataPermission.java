package com.orvillex.annotation;

import java.lang.annotation.*;

/**
 * 过滤数据权限
 * @author y-z-f
 * @version 0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * Entity 中的字段名称
     */
    String fieldName() default "";

    /**
     * Entity 中的部门关联的字段名称
     */
    String joinName() default "";
}
