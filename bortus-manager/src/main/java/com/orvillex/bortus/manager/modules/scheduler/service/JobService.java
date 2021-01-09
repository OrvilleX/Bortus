package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.Date;
import java.util.Map;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;

public interface JobService {
    /**
     * 大屏数据
     */
    Map<String,Long> dashboardInfo();

    /**
     * 图表信息
     */
    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

    /**
     * 停止任务
     */
    ReturnT<String> stop(Long id);

    /**
     * 开始任务
     */
    ReturnT<String> start(Long id);

    /**
     * 删除任务
     */
    ReturnT<String> remove(Long id);

    /**
     * 更新任务
     */
    ReturnT<String> update(JobInfo jobInfo);

    /**
     * 添加任务
     */
    ReturnT<String> add(JobInfo jobInfo);
}
