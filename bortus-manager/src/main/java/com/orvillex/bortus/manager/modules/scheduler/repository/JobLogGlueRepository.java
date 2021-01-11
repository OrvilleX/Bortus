package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务Glue日志
 * @author y-z-f
 * @version 0.1
 */
public interface JobLogGlueRepository extends JpaRepository<JobLogGlue, Long>, JpaSpecificationExecutor<JobLogGlue> {
    
    List<JobLogGlue> findByJobId(Long jobId);

    @Query(value = "DELETE FROM sys_job_logglue WHERE logglue_id NOT IN( SELECT logglue_id FROM( " + 
    "SELECT logglue_id FROM sys_job_logglue WHERE job_id = ?1 ORDER BY update_time DESC LIMIT 0, ?2) t" + 
    ") AND job_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void removeOld(Long jobId, Integer limit);

    @Query(value = "DELETE FROM sys_job_logglue WHERE job_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByJobId(Long jobId);
}
