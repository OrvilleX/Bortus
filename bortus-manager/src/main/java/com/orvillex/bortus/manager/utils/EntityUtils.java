package com.orvillex.bortus.manager.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityUtils {
    private static final Logger log = LoggerFactory.getLogger(EntityUtils.class);

    public static <T> T caseEntity(Object[] entity, Class<T> clazz) {
        if (entity == null || entity.length <= 0) {
            return null;
        }

        List<Map<String, Class<?>>> attributeInfoList = getFieldsInfo(clazz);
        Class[] c2 = new Class[attributeInfoList.size()];
        if (attributeInfoList.size() != entity.length) {
            return null;
        }

        for (int i = 0; i < attributeInfoList.size(); i++) {
            c2[i] = attributeInfoList.get(i).get("type");
        }

        try {
            Constructor<T> constructor = clazz.getConstructor(c2);
            return constructor.newInstance(entity);
        } catch (Exception ex) {
            log.error("实体数据转化为实体类发生异常：{}", ex.getMessage());
        }
        return null;
    }

    private static List<Map<String, Class<?>>> getFieldsInfo(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Map<String, Class<?>>> list = new ArrayList<>(fields.length);
        Map<String, Class<?>> infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap<String, Class<?>>(3);
            infoMap.put("type", fields[i].getType());
            list.add(infoMap);
        }
        return list;
    }
}
