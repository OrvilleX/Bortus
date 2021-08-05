package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.repository.BaseRepository;

import org.springframework.data.jpa.repository.Query;

/**
 * 执行器组仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface JobGroupRepository extends BaseRepository<JobGroup, Long> {
    
    /**
     * 根据执行器地址类型查询
     */
    @Query(value = "SELECT m.* FROM sys_job_group AS m WHERE m.address_type = ?1 ORDER BY m.app_name, m.title, m.group_id ASC", nativeQuery = true)
    List<JobGroup> findByAddressType(int addressType);
}
