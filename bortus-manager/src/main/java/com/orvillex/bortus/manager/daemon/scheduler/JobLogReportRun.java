package com.orvillex.bortus.manager.daemon.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogReport;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogReportService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.LogReportDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogReportRun implements Runnable {
    private static Logger log = LoggerFactory.getLogger(JobFailMonitorRun.class);

    private final JobLogService jobLogService;
    private final JobLogReportService jobLogReportService;

    private volatile boolean toStop = false;

    public JobLogReportRun(JobLogService logService, JobLogReportService logReportService) {
        jobLogService = logService;
        jobLogReportService = logReportService;
    }

    @Override
    public void run() {
        while (!toStop) {

            try {
                for (int i = 0; i < 3; i++) {
                    Calendar itemDay = Calendar.getInstance();
                    itemDay.add(Calendar.DAY_OF_MONTH, -i);
                    itemDay.set(Calendar.HOUR_OF_DAY, 0);
                    itemDay.set(Calendar.MINUTE, 0);
                    itemDay.set(Calendar.SECOND, 0);
                    itemDay.set(Calendar.MILLISECOND, 0);

                    Date todayFrom = itemDay.getTime();

                    itemDay.set(Calendar.HOUR_OF_DAY, 23);
                    itemDay.set(Calendar.MINUTE, 59);
                    itemDay.set(Calendar.SECOND, 59);
                    itemDay.set(Calendar.MILLISECOND, 999);

                    Date todayTo = itemDay.getTime();

                    JobLogReport jobLogReport = new JobLogReport();
                    jobLogReport.setTriggerDay(todayFrom);
                    jobLogReport.setRunningCount(0l);
                    jobLogReport.setSucCount(0l);
                    jobLogReport.setFailCount(0l);

                    LogReportDto triggerCountMap = jobLogService.findLogReport(todayFrom, todayTo);
                    if (triggerCountMap!=null) {
                        Long triggerDayCount = triggerCountMap.getTriggerDayCount();
                        Long triggerDayCountRunning = triggerCountMap.getTriggerDayCountRunning();
                        Long triggerDayCountSuc = triggerCountMap.getTriggerDayCountSuc();
                        Long triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                        jobLogReport.setRunningCount(triggerDayCountRunning);
                        jobLogReport.setSucCount(triggerDayCountSuc);
                        jobLogReport.setFailCount(triggerDayCountFail);
                    }

                    int ret = jobLogReportService.updateByTriggerDay(jobLogReport);
                    if (ret < 1) {
                        jobLogReportService.save(jobLogReport);
                    }
                }
            } catch (Exception e) {
                if (!toStop) {
                    log.error("bortus, job log report thread error:{}", e);
                }
            }

            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (Exception e) {
                if (!toStop) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.info("bortus, job log report thread stop");
    }
    
    public void toStop() {
        toStop = true;
    }
}
