package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.LogReportDto;

import org.springframework.data.domain.Pageable;

public interface JobLogService {
    
    /**
     * 分页查询
     */
    BasePage<JobLog> queryAll(JobLogCriteria criteria, Pageable pageable);

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

    /**
     * 更新触发信息
     */
    void updateTriggerInfo(JobLog xxlJobLog);

    /**
     * 更新执行器信息
     */
    void updateHandleInfo(JobLog xxlJobLog);

    /**
     * 获取日志汇报信息
     */
    LogReportDto findLogReport(Date from, Date to);

    /**
     * 获取丢失的编号
     */
    List<Long> findLostJobIds(Date losedTime);

    /**
     * 获取失败日志编号
     */
    List<Long> findFailJobLogIds(Long pagesize);

    /**
     * 更新告警状态
     */
    Integer updateAlarmStatus(Long id, Integer oldAlarmStatus, Integer newAlarmStatus);
}
