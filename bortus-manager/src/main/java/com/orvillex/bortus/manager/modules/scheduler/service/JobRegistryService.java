package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Set;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;

/**
 * 执行器注册服务
 * @author y-z-f
 * @version 0.1
 */
public interface JobRegistryService {
    
    /**
     * 根据ID删除
     */
    void delete(Set<Long> ids);

    /**
     * 保存
     */
    void save(JobRegistry jobRegistry);

    void update(JobRegistry jobRegistry);
}
