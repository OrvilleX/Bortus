package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;

/**
 * 任务Glue日志
 * @author y-z-f
 * @version 0.1
 */
public interface JobLogGlueService {

    /**
     * 创建
     */
    void create(JobLogGlue jobLogGlue);

    /**
     * 根据任务编号查询
     */
    List<JobLogGlue> findByJobId(Long jobId);

    /**
     * 删除旧日志
     */
    void removeOld(Long jobId, Integer limit);

    /**
     * 根据任务编号删除日志
     */
    void deleteByJobId(Long jobId);
}
