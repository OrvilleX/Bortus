package com.orvillex.bortus.manager.daemon.scheduler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.util.DateUtil;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLosedMonitorRun implements Runnable {
    private static Logger log = LoggerFactory.getLogger(JobLosedMonitorRun.class);

    private volatile boolean toStop = false;

    private final JobLogService jobLogService;

    public JobLosedMonitorRun(JobLogService logService) {
        jobLogService = logService;
    }

    @Override
    public void run() {
        while (!toStop) {
            try {
                Date losedTime = DateUtil.addMinutes(new Date(), -10);
                List<Integer> losedJobIds = jobLogService.findLostJobIds(losedTime);

                if (losedJobIds != null && losedJobIds.size() > 0) {
                    for (Integer logId : losedJobIds) {

                        JobLog jobLog = new JobLog();
                        jobLog.setId(Long.valueOf(logId));

                        jobLog.setHandleTime(new Date());
                        jobLog.setHandleCode(ReturnT.FAIL_CODE);
                        jobLog.setHandleMsg(I18nUtil.getString("joblog_lost_fail"));

                        jobLogService.updateHandleInfo(jobLog);
                    }
                }
            } catch (Exception e) {
                if (!toStop) {
                    log.error("bortus, job fail monitor thread error:{}", e);
                }
            }

            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (Exception e) {
                if (!toStop) {
                    log.error(e.getMessage(), e);
                }
            }

        }

        log.info("bortus, JobLosedMonitorRun stop");
    }

    public void toStop() {
        toStop = true;
    }
}
