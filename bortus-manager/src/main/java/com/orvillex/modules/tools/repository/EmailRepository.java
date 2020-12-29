package com.orvillex.modules.tools.repository;

import com.orvillex.modules.tools.domain.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 邮件服务仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface EmailRepository extends JpaRepository<EmailConfig, Long> {
}