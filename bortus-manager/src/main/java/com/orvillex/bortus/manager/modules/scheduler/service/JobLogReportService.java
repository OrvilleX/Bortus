package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogReport;

public interface JobLogReportService {

    /**
     * 保存
     */
    void save(JobLogReport jobLogReport);

    /**
     * 更新
     */
    Integer updateByTriggerDay(JobLogReport jobLogReport);

    /**
     * 查询指定时间范围内的报表
     */
    List<JobLogReport> queryLogReport(Date triggerDayFrom,Date triggerDayTo);

    /**
     * 查询总计数
     */
    JobLogReport queryLogReportTotal();
}
