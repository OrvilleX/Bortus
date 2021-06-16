package com.orvillex.bortus.manager.modules.system.repository;

import com.orvillex.bortus.manager.modules.system.domain.WxUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 微信用户仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface WxUserRepository extends JpaRepository<WxUser, Long>, JpaSpecificationExecutor<WxUser> {
    
}
