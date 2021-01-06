package com.orvillex.bortus.manager.alarm;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;

/**
 * 各类告警需要实现的接口，用于其他如邮件、短信等服务
 * 
 * @author y-z-f
 * @version 0.1
 */
public interface JobAlarm {

    /**
     * 任务告警
     */
    public boolean doAlarm(JobInfo info, JobLog jobLog);
}
