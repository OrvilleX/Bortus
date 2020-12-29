package com.orvillex.utils;

/**
 * 静态变量
 * @author y-z-f
 * @version 0.1
 */
public class Constant {
    
    /**
     * 用于IP定位转换
     */
    public static final String REGION = "内网IP|内网IP";

    /**
     * win 系统
     */
    public static final String WIN = "win";

    /**
     * mac 系统
     */
    public static final String MAC = "mac";

    /**
     * 常用接口地址
     */
    public static class Url {
        /**
         * IP归属地查询
         */
        public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";
    }
}
