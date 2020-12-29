package com.orvillex.bortus.manager.modules.security.service;

import com.orvillex.bortus.manager.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户登录信息缓存清理服务
 * @author y-z-f
 * @version 0.1
 */
@Component
public class UserCacheClean {

    /**
     * 清理特定用户缓存信息<br>
     * 用户信息变更时
     */
    public void cleanUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            UserDetailsServiceImpl.userDtoCache.remove(userName);
        }
    }

    /**
     * 清理所有用户的缓存信息<br>
     * ,如发生角色授权信息变化，可以简便的全部失效缓存
     */
    public void cleanAll() {
        UserDetailsServiceImpl.userDtoCache.clear();
    }
}
