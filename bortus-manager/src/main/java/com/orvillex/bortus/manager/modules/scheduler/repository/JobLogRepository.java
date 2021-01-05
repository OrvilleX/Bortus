package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.Date;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 任务日志仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface JobLogRepository extends JpaRepository<JobLog, Long>, JpaSpecificationExecutor<JobLog> {
    
    @Query(value = "UPDATE sys_job_log SET trigger_time = ?2, trigger_code = ?3, triggerMsg = ?4," + 
    "executor_address = ?5, executor_handler = ?6, executor_param = ?7, executor_sharding_param = ?8," + 
    "executor_fail_retry_count = ?9 WHERE log_id = ?1", nativeQuery = true)
    @Modifying
    void updateTriggerInfo(Long id, Date triggerTime, Integer triggerCode, String triggerMsg,
        String executorAddress, String executorHandler, String executorParam, String executorShardingParam,
        Integer executorFailRetryCount);
    
    @Query(value = "UPDATE sys_job_log SET handle_time = ?2, handle_code = ?3, handle_msg = ?4 WHERE log_id = ?1", nativeQuery = true)
    @Modifying
    void updateHandleInfo(Long id, Date handleTime, Integer handleCode, String handlerMsg);

    @Query(value = "SELECT COUNT(handle_code) triggerDayCount," + 
    "SUM(CASE WHEN (trigger_code in (0, 200) and handle_code = 0) then 1 else 0 end) as triggerDayCountRunning," + 
    "SUM(CASE WHEN handle_code = 200 then 1 else 0 end) as triggerDayCountSuc " + 
    "FROM sys_job_log WHERE trigger_time BETWEEN ?1 AND ?2", nativeQuery = true)
    Object[] findLogReport(Date from, Date to);
}
