package com.orvillex.bortus.manager.modules.scheduler.service;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;

import org.springframework.data.domain.Pageable;

public interface JobInfoService {
    
    /**
     * 分页查询
     */
    Object queryAll(JobInfoCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     */
    List<JobInfo> queryAll(JobInfoCriteria criteria);

    /**
     * 创建
     */
    void create(JobInfo jobInfo);

    /**
     * 编辑
     */
    void update(JobInfo jobInfo);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 根据ID查询
     */
    JobInfo findById(Long id);

    /**
     * 根据任务组查询
     */
    List<JobInfo> findByJobGroup(Long jobGroup);

    /**
     * 根据执行时间查询
     */
    List<JobInfo> scheduleJobQuery(Long maxNextTime, Integer pageSize);

    /**
     * 更新触发事件和状态
     */
    void scheduleUpdate(Long id, Long triggerLastTime, Long triggerNextTime, Integer triggerStatus);

    Long findAllCount();
}
