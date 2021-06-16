package com.orvillex.bortus.manager.modules.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录凭据
 * @author y-z-f
 * @version 0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginByCodeVo {
    String code;
    String nickName;
    Integer gender;
    String city;
    String province;
    String country;
    String avatarUrl;
    String encryptedData;
    String iv;
}
