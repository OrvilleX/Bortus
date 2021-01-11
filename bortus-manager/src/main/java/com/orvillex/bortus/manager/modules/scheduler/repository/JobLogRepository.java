package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    void updateTriggerInfo(Long id, Date triggerTime, Integer triggerCode, String triggerMsg,
        String executorAddress, String executorHandler, String executorParam, String executorShardingParam,
        Integer executorFailRetryCount);
    
    @Query(value = "UPDATE sys_job_log SET handle_time = ?2, handle_code = ?3, handle_msg = ?4 WHERE log_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void updateHandleInfo(Long id, Date handleTime, Integer handleCode, String handlerMsg);

    @Query(value = "SELECT COUNT(handle_code) triggerDayCount," + 
    "SUM(CASE WHEN (trigger_code in (0, 200) and handle_code = 0) then 1 else 0 end) as triggerDayCountRunning," + 
    "SUM(CASE WHEN handle_code = 200 then 1 else 0 end) as triggerDayCountSuc " + 
    "FROM sys_job_log WHERE trigger_time BETWEEN ?1 AND ?2", nativeQuery = true)
    Object[] findLogReport(Date from, Date to);

    @Query(value = "SELECT log_id FROM sys_job_log WHERE !((trigger_code in (0, 200) AND handle_code = 0) OR (handle_code = 200)) " + 
    "AND `alarm_status` = 0 ORDER BY log_id ASC LIMIT ?1", nativeQuery = true)
    List<Long> findFailJobLogIds(Long pagesize);

    @Query(value = "SELECT m.log_id FROM sys_job_log AS m WHERE m.trigger_code = 200 AND m.handle_code = 0 " + 
    "AND m.trigger_time <= ?1 AND m.executor_address NOT IN (SELECT t.registry_value FROM sys_job_registry AS t)", nativeQuery = true)
    List<Long> findLostJobIds(Date losedTime);

    @Query(value = "UPDATE sys_job_log SET alarm_status = ?3 WHERE log_id = ?1 AND alarm_status = ?2", nativeQuery = true)
    @Modifying
    @Transactional
    Integer updateAlarmStatus(Long id, Integer oldAlarmStatus, Integer newAlarmStatus);
}
