package com.orvillex.bortus.annotation;

import java.lang.annotation.*;

/**
 * 标记接口可以匿名访问
 * @author y-z-f
 * @version 0.1
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousAccess {
    
}
