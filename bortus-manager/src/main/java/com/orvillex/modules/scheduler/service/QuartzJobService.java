package com.orvillex.modules.scheduler.service;

import com.orvillex.modules.scheduler.domain.QuartzJob;
import com.orvillex.modules.scheduler.domain.QuartzLog;
import com.orvillex.modules.scheduler.service.dto.JobQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 调度任务服务接口
 * @author y-z-f
 * @version 0.1
 */
public interface QuartzJobService {

    /**
     * 分页查询
     */
    Object queryAll(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     */
    List<QuartzJob> queryAll(JobQueryCriteria criteria);

    /**
     * 分页查询日志
     */
    Object queryAllLog(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     */
    List<QuartzLog> queryAllLog(JobQueryCriteria criteria);

    /**
     * 创建
     */
    void create(QuartzJob resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(QuartzJob resources);

    /**
     * 删除任务
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 根据ID查询
     */
    QuartzJob findById(Long id);

    /**
     * 更改定时任务状态
     * @param quartzJob /
     */
    void updateIsPause(QuartzJob quartzJob);

    /**
     * 立即执行定时任务
     * @param quartzJob /
     */
    void execution(QuartzJob quartzJob);

    /**
     * 导出定时任务
     */
    void download(List<QuartzJob> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 导出定时任务日志
     */
    void downloadLog(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException;

    /**
     * 执行子任务
     */
    void executionSubJob(String[] tasks) throws InterruptedException;
}
