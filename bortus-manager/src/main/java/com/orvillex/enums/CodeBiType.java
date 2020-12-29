package com.orvillex.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码场景
 * @author y-z-f
 * @version 0.1
 */
@Getter
@AllArgsConstructor
public enum CodeBiType {
    
    ONE(1, "旧邮箱修改邮箱"),
    TWO(2, "通过邮箱修改密码");

    private final Integer code;
    private final String description;

    public static CodeBiType find(Integer code) {
        for (CodeBiType value : CodeBiType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
