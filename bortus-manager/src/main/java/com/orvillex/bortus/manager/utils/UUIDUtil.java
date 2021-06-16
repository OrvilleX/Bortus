package com.orvillex.bortus.manager.utils;

import java.util.UUID;

/**
 * UUID 工具类
 * @author y-z-f
 * @version 0.1
 */
public class UUIDUtil {
    /**
     * 带-的UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
 
    /**
     * 去掉-的UUID
     */
    public static String getUUID2() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
