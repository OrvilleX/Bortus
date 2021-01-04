package com.orvillex.bortus.job.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具
 */
public class ThrowableUtil {
    
    /**
     * 异常以标准字符串输出
     */
    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }
}
