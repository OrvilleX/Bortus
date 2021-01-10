package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.List;
import java.util.Set;

import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;

import org.springframework.data.domain.Pageable;

public interface JobGroupService {
    
    /**
     * 分页查询
     */
    BasePage<JobGroup> queryAll(JobGroupCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     */
    List<JobGroup> queryAll(JobGroupCriteria criteria);

    /**
     * 根据执行器地址类型查询
     */
    List<JobGroup> findByAddressType(Integer addressType);

    /**
     * 创建
     */
    void create(JobGroup resources);

    /**
     * 编辑
     */
    void update(JobGroup resources);
    
    /**
     * 删除
     */
    void delete(Set<Long> ids);

    /**
     * 根据ID查询
     */
    JobGroup findById(Long id);
}
