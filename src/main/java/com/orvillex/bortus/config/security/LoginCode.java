package com.orvillex.bortus.config.security;

import com.orvillex.bortus.enums.LoginCodeType;
import lombok.Data;

/**
 * 验证码配置
 * @author y-z-f
 * @version 0.1
 */
@Data
public class LoginCode {
    /**
     * 验证码配置
     */
    private LoginCodeType codeType;

    /**
     * 验证码有效期，单位分钟
     */
    private Long expiration = 2L;

    /**
     * 验证码内容长度
     */
    private Integer length = 2;

    /**
     * 验证码宽度
     */
    private Integer width = 111;

    /**
     * 验证码高度
     */
    private Integer height = 36;

    /**
     * 验证码字体
     */
    private String fontName;

    /**
     * 字体大小
     */
    private Integer fontSize = 25;

    public LoginCodeType getCodeType() {
        return codeType;
    }
}
