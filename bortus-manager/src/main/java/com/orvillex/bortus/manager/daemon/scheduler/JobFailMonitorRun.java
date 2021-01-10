package com.orvillex.bortus.manager.daemon.scheduler;

import java.util.List;

import com.orvillex.bortus.manager.alarm.JobAlarmer;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobFailMonitorRun implements Runnable {
    private static Logger log = LoggerFactory.getLogger(JobFailMonitorRun.class);

    private final JobLogService jobLogService;
    private final JobInfoService jobInfoService;
    private final JobAlarmer jobAlarm;
    private final JobTriggerPool jobTriggerPool;

    private volatile boolean toStop = false;

    public JobFailMonitorRun(JobLogService logService, JobInfoService infoService, JobAlarmer alarm,
            JobTriggerPool triggerPool) {
        jobLogService = logService;
        jobInfoService = infoService;
        jobAlarm = alarm;
        jobTriggerPool = triggerPool;
    }

    @Override
    public void run() {
        while (!toStop) {
            try {
                List<Long> failLogIds = jobLogService.findFailJobLogIds(1000L);
                if (failLogIds != null && !failLogIds.isEmpty()) {
                    for (long failLogId : failLogIds) {

                        int lockRet = jobLogService.updateAlarmStatus(failLogId, 0, -1);
                        if (lockRet < 1) {
                            continue;
                        }
                        JobLog log = jobLogService.findById(failLogId);
                        JobInfo info = jobInfoService.findById(log.getJobId());

                        if (log.getExecutorFailRetryCount() > 0) {
                            jobTriggerPool.trigger(log.getJobId(), TriggerTypeEnum.RETRY,
                                    (log.getExecutorFailRetryCount() - 1), log.getExecutorShardingParam(),
                                    log.getExecutorParam(), null);
                            String retryMsg = "<br><br><span style=\"color:#F39C12;\" >"
                                    + I18nUtil.getString("jobconf_trigger_type_retry") + "</span><br>";
                            log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
                            jobLogService.updateTriggerInfo(log);
                        }

                        int newAlarmStatus = 0;
                        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
                            boolean alarmResult = jobAlarm.alarm(info, log);
                            newAlarmStatus = alarmResult ? 2 : 3;
                        } else {
                            newAlarmStatus = 1;
                        }

                        jobLogService.updateAlarmStatus(failLogId, -1, newAlarmStatus);
                    }
                }
            } catch (Exception e) {
                if (!toStop) {
                    log.error("job fail monitor thread error:{}", e);
                }
            }
        }
    }

    public void toStop() {
        toStop = true;
    }
}