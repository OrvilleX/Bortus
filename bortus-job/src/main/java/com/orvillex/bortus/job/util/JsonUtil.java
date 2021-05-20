package com.orvillex.bortus.job.util;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
 
    /**
     * 对象转换为json字符串
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            logger.error("json序列化出错：" + obj, e);
            return null;
        }
    }
 
    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return JSON.parseObject(json, tClass);
        } catch (Exception e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }
}
