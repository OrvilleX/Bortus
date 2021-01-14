package com.orvillex.bortus.manager.daemon;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.orvillex.bortus.manager.alarm.JobAlarmer;
import com.orvillex.bortus.manager.config.scheduler.SchedulerProperties;
import com.orvillex.bortus.manager.config.thread.AsyncTaskProperties;
import com.orvillex.bortus.manager.daemon.scheduler.JobFailMonitorRun;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.utils.JobSchedulerUtils;
import com.orvillex.bortus.manager.utils.ThreadPoolExecutorUtil;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaemonRun implements InitializingBean, DisposableBean {
    private ThreadPoolExecutor pool;
    private JobFailMonitorRun jobFailMonitor;

    @Resource
    private SchedulerProperties schedulerProperties;

    @Resource
    private AsyncTaskProperties asyncTaskProperties;

    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private JobAlarmer alarm;
    @Autowired
    private JobTriggerPool jobTriggerPool;

    @Override
    public void destroy() throws Exception {
        jobFailMonitor.toStop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = ThreadPoolExecutorUtil.getPoll(asyncTaskProperties);
        JobSchedulerUtils.ACCESSTOKEN = schedulerProperties.getAccessToken();
        jobFailMonitor = new JobFailMonitorRun(jobLogService, jobInfoService, alarm, jobTriggerPool);

        pool.execute(jobFailMonitor);
    }
}
