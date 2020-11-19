package com.orvillex.bortus.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具
 * @author y-z-f
 * @version 0.1
 */
public class ThrowableUtil {

    /**
     * 获取异常信息
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
