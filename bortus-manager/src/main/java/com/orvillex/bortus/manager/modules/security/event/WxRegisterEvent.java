package com.orvillex.bortus.manager.modules.security.event;

import com.orvillex.bortus.manager.modules.system.domain.WxUser;

import org.springframework.context.ApplicationEvent;

/**
 * 微信登录事件
 * @author y-z-f
 * @version 0.1
 */
public class WxRegisterEvent extends ApplicationEvent {
    private WxUser user;

    public WxRegisterEvent(WxUser source) {
        super(source);
        this.user = source;
    }
    
    public WxUser getUser() {
        return user;
    }
}
