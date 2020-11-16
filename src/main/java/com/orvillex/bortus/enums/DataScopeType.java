package com.orvillex.bortus.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 * @author y-z-f
 * @version 0.1
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {
    
    ALL("全部", "全部数据权限"),
    THIS_LEVEL("本级", "自己部门的数据权限"),
    CUSTOMIZE("自定义", "自定义的数据权限");

    private final String value;
    private final String description;

    public static DataScopeType find(String val) {
        for (DataScopeType dataScopeType : DataScopeType.values()) {
            if (val.equals(dataScopeType.getValue())) {
                return dataScopeType;
            }
        }
        return null;
    }
}
