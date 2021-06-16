package com.orvillex.bortus.manager.modules.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录响应
 * @author y-z-f
 * @version 0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCodeReply {
    boolean isNew;
    String token;
}
