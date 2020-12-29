package com.orvillex.bortus.manager.enums;

import lombok.Getter;

/**
 * 日志类型
 * @author y-z-f
 * @version 0.1
 */
@Getter
public enum LogActionType {

    ADD("新增"),
    SELECT("查询"),
    UPDATE("更新"),
    DELETE("删除");

    private String value;

    LogActionType(String value) {
        this.value = value;
    }
}
