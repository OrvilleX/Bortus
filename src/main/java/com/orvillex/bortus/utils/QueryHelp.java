package com.orvillex.bortus.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;;
import lombok.extern.slf4j.Slf4j;
import com.orvillex.bortus.annotation.DataPermission;
import com.orvillex.bortus.annotation.Query;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 查询表达式转换工具类
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
@SuppressWarnings({"unchecked", "all"})
public class QueryHelp {
    
    public static <R,Q> Predicate getPredicate(Root<R> root, Q query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();
        if (query == null) {
            return cb.and(list.toArray(new Predicate[0]));
        }
        DataPermission permission = query.getClass().getAnnotation(DataPermission.class);
        if (permission != null) {
            List<Long> dataScopes = SecurityUtils.getCurrentUserDataScope();
            if (CollectionUtil.isNotEmpty(dataScopes)) {
                if (StringUtils.isNotBlank(permission.joinName()) && StringUtils.isNotBlank(permission.fieldName())) {
                    Join join = root.join(permission.joinName(), JoinType.LEFT);
                    list.add(getExpression(permission.fieldName(), join, root).in(dataScopes));
                }
            }
        }
    }

    /**
     * 获取对象所有字段
     */
    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Expression<T> getExpression(String attributeName, Join join, Root<R> root) {
        if (ObjectUtil.isNotEmpty(join)) {
            return join.get(attributeName);
        } else {
            return root.get(attributeName);
        }
    }

    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
