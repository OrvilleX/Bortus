package com.orvillex.bortus.manager.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 多语言工具类
 * @author y-z-f
 * @version 0.1
 */
public class I18nUtil {
    private static Logger logger = LoggerFactory.getLogger(I18nUtil.class);

    private static Properties prop = null;

    private static String i18n = "zh_CN";

    public static Properties loadI18nProp(){
        if (prop != null) {
            return prop;
        }
        try {
            String i18nFile = MessageFormat.format("i18n/message_{0}.properties", i18n);

            Resource resource = new ClassPathResource(i18nFile);
            EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
            prop = PropertiesLoaderUtils.loadProperties(encodedResource);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return prop;
    }

    /**
     * 获取对应Key语言
     */
    public static String getString(String key) {
        return loadI18nProp().getProperty(key);
    }

    /**
     * 获取多个Key对应的语言，结果为Json
     */
    public static String getMultString(String... keys) {
        Map<String, String> map = new HashMap<String, String>();

        Properties prop = loadI18nProp();
        if (keys!=null && keys.length>0) {
            for (String key: keys) {
                map.put(key, prop.getProperty(key));
            }
        } else {
            for (String key: prop.stringPropertyNames()) {
                map.put(key, prop.getProperty(key));
            }
        }

        String json = JSON.toJSONString(map);
        return json;
    }
}
