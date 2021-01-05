package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 任务信息仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface JobInfoRepository extends JpaRepository<JobInfo, Long>, JpaSpecificationExecutor<JobInfo> {
    
    List<JobInfo> findByJobGroup(Integer jobGroup);

    @Query(value = "SELECT m.* FROM sys_job_info AS m WHERE m.trigger_status = 1 AND t.trigger_next_time <= ?1 ORDER BY info_id ASC LIMIT ?2", nativeQuery = true)
    List<JobInfo> scheduleJobQuery(Long maxNextTime, Integer pageSize);

    @Modifying
    @Query(value = "UPDATE sys_job_info SET trigger_last_time = ?2, trigger_next_time = ?3, trigger_status = ?4 WHERE info_id = ?1", nativeQuery = true)
    void scheduleUpdate(Long id, Long triggerLastTime, Long triggerNextTime, Integer triggerStatus);
}
