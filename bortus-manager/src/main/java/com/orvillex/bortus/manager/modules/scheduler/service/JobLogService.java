package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Set;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogCriteria;

import org.springframework.data.domain.Pageable;

public interface JobLogService {
    
    /**
     * 分页查询
     */
    Object queryAll(JobLogCriteria criteria, Pageable pageable);

    /**
     * 创建
     */
    void create(JobLog jobLog);

    /**
     * 编辑
     */
    void update(JobLog jobLog);

    /**
     * 删除
     */
    void delete(Set<Long> ids);

    /**
     * 根据ID查询
     */
    JobLog findById(Long id);
}
