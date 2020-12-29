package com.orvillex.modules.tools.service;

import com.orvillex.modules.tools.domain.EmailConfig;
import com.orvillex.modules.tools.domain.vo.EmailVo;

/**
 * 邮件服务
 * @author y-z-f
 * @version 0.1
 */
public interface EmailService {

    /**
     * 更新邮件配置
     */
    EmailConfig config(EmailConfig emailConfig, EmailConfig old) throws Exception;

    /**
     * 查询配置
     */
    EmailConfig find();

    /**
     * 发送邮件
     */
    void send(EmailVo emailVo, EmailConfig emailConfig);
}
