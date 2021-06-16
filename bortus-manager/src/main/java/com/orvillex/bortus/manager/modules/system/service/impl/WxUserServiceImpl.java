package com.orvillex.bortus.manager.modules.system.service.impl;

import com.orvillex.bortus.manager.modules.system.domain.WxUser;
import com.orvillex.bortus.manager.modules.system.repository.WxUserRepository;
import com.orvillex.bortus.manager.modules.system.service.WxUserService;
import com.orvillex.bortus.manager.utils.ValidationUtil;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 微信用户服务实现
 * @author y-z-f
 * @version 0.1
 */
@Service
@RequiredArgsConstructor
public class WxUserServiceImpl implements WxUserService {
    private final WxUserRepository wxUserRepository;

    @Override
    public Long create(WxUser user) {
        return wxUserRepository.save(user).getId();
    }

    @Override
    public void update(WxUser user) {
        WxUser existedTenant = wxUserRepository.findById(user.getId()).orElseGet(WxUser::new);
        ValidationUtil.isNull(existedTenant.getId(), "WxUser", "id", user.getId());
        user.setId(existedTenant.getId());
        wxUserRepository.save(user);
    }

    @Override
    public WxUser findByWxOpenId(String wxOpenId) {
        WxUser user = wxUserRepository.findOne((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("wxOpenId").as(String.class), wxOpenId);
        }).orElse(null);
        return user;
    }

    @Override
    public WxUser findBySessionKey(String sessionKey) {
        WxUser user = wxUserRepository.findOne((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("sessionKey").as(String.class), sessionKey);
        }).orElse(null);
        return user;
    }

    @Override
    public WxUser findById(Long id) {
        WxUser info = wxUserRepository.findById(id).orElseGet(WxUser::new);
        ValidationUtil.isNull(info.getId(), "WxUser", "id", id);
        return info;
    }
    
}
