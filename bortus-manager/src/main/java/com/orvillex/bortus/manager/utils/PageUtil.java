package com.orvillex.bortus.manager.utils;

import org.springframework.data.domain.Page;
import java.util.*;

import com.orvillex.bortus.manager.entity.BasePage;


/**
 * 分页工具类
 * @author y-z-f
 * @version 0.1
 */
public class PageUtil extends cn.hutool.core.util.PageUtil {
    
    /**
     * List 分页
     */
    public static List<?> toPage(int page, int size, List<?> list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;
        int count = list.size();
        if (fromIndex > count) {
            return new ArrayList<>();
        } else if (toIndex >= count) {
            return list.subList(fromIndex, count);
        } else {
            return list.subList(fromIndex, toIndex);
        }
    }

    /**
     * Page 数据处理
     */
    public static <T> BasePage<T> toPage(Page<T> page) {
        BasePage<T> basePage = new BasePage<T>();
        basePage.setContent(page.getContent());
        basePage.setTotalElements(page.getTotalElements());
        return basePage;
    }

    /**
     * 自定义分页
     */
    public static <T> BasePage<T> toPage(List<T> content, Long totalElements) {
        BasePage<T> basePage = new BasePage<T>();
        basePage.setContent(content);
        basePage.setTotalElements(totalElements);
        return basePage;
    }
}
