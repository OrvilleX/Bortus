package com.orvillex.bortus.manager.modules.scheduler.repository;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 调度日志报表仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface JobLogReportRepository extends JpaRepository<JobLogReport, Long>, JpaSpecificationExecutor<JobLogReport> {
    
    @Query(value = "UPDATE JobLogReport SET runningCount = :#{#joblogreport.runningCount}," + 
    "sucCount = :#{#joblogreport.sucCount}, failCount = :#{#joblogreport.failCount} WHERE triggerDay = :#{#joblogreport.triggerDay}")
    @Modifying
    void updateByTriggerDay(@Param("joblogreport") JobLogReport jobLogReport);

    @Query(value = "SELECT m.* FROM sys_job_logreport AS m WHERE m.trigger_day between ?1 AND ?2 ORDER BY m.trigger_day ASC", nativeQuery = true)
    List<JobLogReport> queryLogReport(Date triggerDayFrom,Date triggerDayTo);

    @Query(value = "SELECT SUM(running_count) running_count, SUM(suc_count) suc_count, SUM(fail_count) fail_count FROM sys_job_logreport", nativeQuery = true)
    JobLogReport queryLogReportTotal();
}
