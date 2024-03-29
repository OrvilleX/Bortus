package com.orvillex.bortus.manager.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.orvillex.bortus.manager.annotation.DataPermission;
import com.orvillex.bortus.manager.annotation.Query;
import lombok.extern.slf4j.Slf4j;

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
                } else if (StringUtils.isBlank(permission.joinName()) && StringUtils.isNotBlank(permission.fieldName())) {
                    list.add(getExpression(permission.fieldName(), null, root).in(dataScopes));
                }
            }
        }
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String joinName = q.joinName();
                    String blurry = q.blurry();
                    String attributeName = isBlank(propName) ? field.getName() : propName;
                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }
                    Join join = null;

                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(",");
                        List<Predicate> orPredicates = new ArrayList<>();
                        for (String s : blurrys) {
                            orPredicates.add(cb.like(root.get(s.trim()).as(String.class), "%" + val.toString() + "%"));
                        }
                        Predicate[] p = new Predicate[orPredicates.size()];
                        list.add(cb.or(orPredicates.toArray(p)));
                        continue;
                    }

                    // 链表
                    if (ObjectUtil.isNotEmpty(joinName)) {
                        String[] joinNames = joinName.split(">");
                        for (String name : joinNames) {
                            switch (q.join()) {
                                case LEFT: {
                                    if (ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)) {
                                        join = join.join(name, JoinType.LEFT);
                                    } else {
                                        join = root.join(name, JoinType.LEFT);
                                    }
                                }
                                break;
                                case RIGHT: {
                                    if (ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)) {
                                        join = join.join(name, JoinType.RIGHT);
                                    } else {
                                        join = root.join(name, JoinType.RIGHT);
                                    }
                                }
                                break;
                                case INNER: {
                                    if (ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)) {
                                        join = join.join(name, JoinType.INNER);
                                    } else {
                                        join = root.join(name, JoinType.INNER);
                                    }
                                }
                                break;
                                default: break;
                            }
                        }
                    }

                    // 常规
                    switch (q.type()) {
                        case EQUAL: {
                            list.add(cb.equal(getExpression(attributeName, join, root).
                            as((Class<? extends Comparable>) fieldType), val));
                        }
                        break;
                        case GREATER_THAN: {
                            list.add(cb.greaterThan(getExpression(attributeName, join, root)
                            .as((Class<? extends Comparable>) fieldType), (Comparable)val));
                        }
                        break;
                        case LESS_THAN: {
                            list.add(cb.lessThanOrEqualTo(getExpression(attributeName, join, root)
                            .as((Class<? extends Comparable>) fieldType), (Comparable) val));
                        }
                        break;
                        case LESS_THAN_NQ: {
                            list.add(cb.lessThan(getExpression(attributeName, join, root)
                            .as((Class<? extends Comparable>) fieldType), (Comparable) val));
                        }
                        break;
                        case INNER_LIKE: {
                            list.add(cb.like(getExpression(attributeName, join, root)
                            .as(String.class), "%" + val.toString() + "%"));
                        }
                        break;
                        case LEFT_LIKE: {
                            list.add(cb.like(getExpression(attributeName, join, root)
                            .as(String.class), "%" + val.toString()));
                        }
                        break;
                        case RIGHT_LIKE: {
                            list.add(cb.like(getExpression(attributeName, join, root)
                            .as(String.class),  val.toString() + "%"));
                        }
                        break;
                        case IN: {
                            if (CollUtil.isNotEmpty((Collection<Long>)val)) {
                                list.add(getExpression(attributeName, join, root).in((Collection<Long>) val));
                            }
                        }
                        break;
                        case NOT_EQUAL: {
                            list.add(cb.notEqual(getExpression(attributeName, join, root), val));
                        }
                        break;
                        case NOT_NULL: {
                            list.add(cb.isNotNull(getExpression(attributeName, join, root)));
                        }
                        break;
                        case IS_NULL: {
                            list.add(cb.isNull(getExpression(attributeName, join, root)));
                        }
                        break;
                        case BETWEEN: {
                            List<Object> between = new ArrayList<>((List<Object>)val);
                            list.add(cb.between(getExpression(attributeName, join, root)
                            .as((Class<? extends Comparable>)between.get(0).getClass()),
                            (Comparable) between.get(0), (Comparable) between.get(1)));
                        }
                        break;
                        default: break;
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        int size = list.size();
        return cb.and(list.toArray(new Predicate[size]));
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
