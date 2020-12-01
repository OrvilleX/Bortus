package com.orvillex.bortus.modules.system.service;

import com.orvillex.bortus.modules.tools.domain.vo.EmailVo;

/**
 * 校验服务
 * @author y-z-f
 * @version 0.1
 */
public interface VerifyService {

    /**
     * 发送验证码
     */
    EmailVo sendEmail(String email, String key);

    /**
     * 验证
     */
    void validated(String key, String code);
}
