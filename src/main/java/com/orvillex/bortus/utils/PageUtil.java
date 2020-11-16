package com.orvillex.bortus.utils;

import org.springframework.data.domain.Page;
import java.util.*;


/**
 * 分页工具类
 * @author y-z-f
 * @version 0.1
 */
public class PageUtil extends cn.hutool.core.util.PageUtil {
    
    /**
     * List 分页
     */
    public static List toPage(int page, int size, List list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;
        int count = list.size();
        if (fromIndex > count) {
            return new ArrayList();
        } else if (toIndex >= count) {
            return list.subList(fromIndex, count);
        } else {
            return list.subList(fromIndex, toIndex);
        }
    }

    /**
     * Page 数据处理
     */
    public static Map<String, Object> toPage(Page page) {
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getContent());
        map.put("totalElements", page.getTotalElements());
        return map;
    }

    /**
     * 自定义分页
     */
    public static Map<String, Object> toPage(Object object, Object totalElements) {
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", object);
        map.put("totalElements", totalElements);
        return map;
    }
}
