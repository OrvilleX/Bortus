package com.orvillex.bortus.manager.aop;

/**
 * 限流依据类型
 * @author y-z-f
 * @version 0.1
 */
public enum LimitType {
    /**
     * 基于用户
     */
    CUSTOMER,
    /**
     * 基于客户端IP
     */
    IP
}
