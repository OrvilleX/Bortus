package com.orvillex.bortus.manager.modules.system.service;

import com.orvillex.bortus.manager.modules.system.domain.WxUser;

public interface WxUserService {
    /**
     * 创建微信用户
     */
    Long create(WxUser user);

    /**
     * 修改微信用户
     */
    void update(WxUser user);

    /**
     * 根据微信openid查询微信用户
     */
    WxUser findByWxOpenId(String wxOpenId);

    /**
     * 根据会话令牌查询微信用户
     */
    WxUser findBySessionKey(String sessionKey);

    /**
     * 根据编号查询
     */
    WxUser findById(Long id);
}
