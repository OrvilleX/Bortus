package com.orvillex.bortus.manager.utils;

import java.math.BigDecimal;

/**
 * 微信支付工具
 * @author y-z-f
 * @version 0.1
 */
public class WxPayUtil {

    /**
     * 元转分
     */
    public static int yuanToFee(BigDecimal value) {
        return value.multiply(new BigDecimal(100)).intValue();
    }

    /**
     * 获取时间戳（秒）
     */
    public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
