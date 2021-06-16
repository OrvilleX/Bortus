package com.orvillex.bortus.manager.daemon;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.orvillex.bortus.manager.alarm.JobAlarmer;
import com.orvillex.bortus.manager.config.scheduler.SchedulerProperties;
import com.orvillex.bortus.manager.config.thread.AsyncTaskProperties;
import com.orvillex.bortus.manager.daemon.scheduler.JobFailMonitorRun;
import com.orvillex.bortus.manager.daemon.scheduler.JobLogReportRun;
import com.orvillex.bortus.manager.daemon.scheduler.JobLosedMonitorRun;
import com.orvillex.bortus.manager.daemon.scheduler.JobRegistryMonitorRun;
import com.orvillex.bortus.manager.daemon.scheduler.JobScheduleRun;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogReportService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobRegistryService;
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
    private JobLogReportRun jobLogReportRun;
    private JobLosedMonitorRun jobLosedMonitorRun;
    private JobRegistryMonitorRun jobRegistryMonitorRun;
    private JobScheduleRun jobScheduleRun;

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
    @Autowired
    private JobLogReportService jobLogReportService;
    @Autowired
    private JobGroupService jobGroupService;
    @Autowired
    private JobRegistryService jobRegistryService;

    @Override
    public void destroy() throws Exception {
        jobFailMonitor.toStop();
        jobLogReportRun.toStop();
        jobLosedMonitorRun.toStop();
        jobRegistryMonitorRun.toStop();
        jobScheduleRun.toStop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = ThreadPoolExecutorUtil.getPoll(asyncTaskProperties);
        JobSchedulerUtils.ACCESSTOKEN = schedulerProperties.getAccessToken();
        jobFailMonitor = new JobFailMonitorRun(jobLogService, jobInfoService, alarm, jobTriggerPool);
        jobLogReportRun = new JobLogReportRun(jobLogService, jobLogReportService);
        jobLosedMonitorRun = new JobLosedMonitorRun(jobLogService);
        jobRegistryMonitorRun = new JobRegistryMonitorRun(jobGroupService, jobRegistryService);
        jobScheduleRun = new JobScheduleRun(schedulerProperties.getTriggerPoolFastMax(), schedulerProperties.getTriggerPoolSlowMax(), jobInfoService, jobTriggerPool);

        pool.execute(jobFailMonitor);
        pool.execute(jobLogReportRun);
        pool.execute(jobLosedMonitorRun);
        pool.execute(jobRegistryMonitorRun);
        pool.execute(jobScheduleRun.getScheduleRun());
        pool.execute(jobScheduleRun.getRingRun());
    }
}
