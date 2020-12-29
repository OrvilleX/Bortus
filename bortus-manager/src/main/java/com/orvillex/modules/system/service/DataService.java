package com.orvillex.modules.system.service;

import com.orvillex.modules.system.service.dto.UserDto;

import java.util.List;

/**
 * 数据权限服务
 * @author y-z-f
 * @version 0.1
 */
public interface DataService {

    /**
     * 获取数据权限
     */
    List<Long> getDeptIds(UserDto user);
}
