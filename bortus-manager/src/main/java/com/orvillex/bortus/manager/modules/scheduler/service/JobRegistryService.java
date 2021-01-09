package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;

/**
 * 执行器注册服务
 * @author y-z-f
 * @version 0.1
 */
public interface JobRegistryService {

    /**
     * 查询过期注册ID
     */
    List<Long> findDead(Integer timeout, Date nowTime);

    /**
     * 查询过期注册
     */
    List<JobRegistry> findAll(Integer timeout, Date nowTime);
    
    /**
     * 根据ID删除
     */
    void delete(Set<Long> ids);

    /**
     * 删除
     */
    void delete(String registryGroup, String registryKey, String registryValue);

    /**
     * 保存
     */
    void save(JobRegistry jobRegistry);

    void update(JobRegistry jobRegistry);

    Integer registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime);

    void registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime);
}
