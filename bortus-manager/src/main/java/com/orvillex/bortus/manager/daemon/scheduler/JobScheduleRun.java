package com.orvillex.bortus.manager.daemon.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.manager.modules.scheduler.core.cron.CronExpression;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduleRun {
    private static Logger log = LoggerFactory.getLogger(JobScheduleRun.class);

    public static final long PRE_READ_MS = 5000; // pre read

    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private volatile static Map<Integer, List<Long>> ringData = new ConcurrentHashMap<>();

    private final Integer triggerPoolFastMax;
    private final Integer triggerPoolSlowMax;
    private final JobInfoService jobInfoService;
    private final JobTriggerPool jobTriggerPool;

    public JobScheduleRun(Integer poolFastMax, Integer poolSlowMax, JobInfoService infoService,
            JobTriggerPool triggerPool) {
        triggerPoolFastMax = poolFastMax;
        triggerPoolSlowMax = poolSlowMax;
        jobInfoService = infoService;
        jobTriggerPool = triggerPool;
        jobTriggerPool.start();
    }

    public Runnable getScheduleRun() {
        return () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                if (!scheduleThreadToStop) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("init bortus scheduler success.");

            int preReadCount = (triggerPoolFastMax + triggerPoolSlowMax) * 20;

            while (!scheduleThreadToStop) {
                long start = System.currentTimeMillis();

                boolean preReadSuc = true;
                try {

                    // 分布式锁开始

                    // 1、pre read
                    long nowTime = System.currentTimeMillis();
                    List<JobInfo> scheduleList = jobInfoService.scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                    if (scheduleList != null && scheduleList.size() > 0) {
                        // 2、push time-ring
                        for (JobInfo jobInfo : scheduleList) {

                            // time-ring jump
                            if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                                // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                log.warn("bortus, schedule misfire, jobId = " + jobInfo.getId());
                                refreshNextValidTime(jobInfo, new Date());

                            } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                jobTriggerPool.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                log.debug("bortus, schedule push trigger : jobId = " + jobInfo.getId());

                                refreshNextValidTime(jobInfo, new Date());

                                // next-trigger-time in 5s, pre-read again
                                if (jobInfo.getTriggerStatus() == 1
                                        && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {
                                    int ringSecond = (int) ((jobInfo.getTriggerNextTime() / 1000) % 60);
                                    pushTimeRing(ringSecond, jobInfo.getId());
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                }

                            } else {
                                // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                int ringSecond = (int) ((jobInfo.getTriggerNextTime() / 1000) % 60);
                                pushTimeRing(ringSecond, jobInfo.getId());
                                refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                            }
                        }

                        // 3、update trigger info
                        for (JobInfo jobInfo : scheduleList) {
                            jobInfoService.scheduleUpdate(jobInfo.getId(), jobInfo.getTriggerLastTime(),
                                    jobInfo.getTriggerNextTime(), jobInfo.getTriggerStatus());
                        }

                    } else {
                        preReadSuc = false;
                    }
                    // tx stop

                } catch (Exception e) {
                    if (!scheduleThreadToStop) {
                        log.error("bortus, JobScheduleHelper#scheduleThread error:{}", e);
                    }
                } finally {

                    // 分布式事务结束
                }
                long cost = System.currentTimeMillis() - start;

                if (cost < 1000) {
                    try {
                        TimeUnit.MILLISECONDS
                                .sleep((preReadSuc ? 1000 : PRE_READ_MS) - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!scheduleThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }

            }
            log.info("bortus, JobScheduleHelper#scheduleThread stop");
        };
    }

    public Runnable getRingRun() {
        return () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                if (!ringThreadToStop) {
                    log.error(e.getMessage(), e);
                }
            }

            while (!ringThreadToStop) {

                try {
                    List<Long> ringItemData = new ArrayList<>();
                    int nowSecond = Calendar.getInstance().get(Calendar.SECOND); // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                    for (int i = 0; i < 2; i++) {
                        List<Long> tmpData = ringData.remove((nowSecond + 60 - i) % 60);
                        if (tmpData != null) {
                            ringItemData.addAll(tmpData);
                        }
                    }

                    log.debug(
                            ">>>>>>>>>>> bortus-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData));
                    if (ringItemData.size() > 0) {
                        // do trigger
                        for (Long jobId : ringItemData) {
                            // do trigger
                            jobTriggerPool.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                        }
                        ringItemData.clear();
                    }
                } catch (Exception e) {
                    if (!ringThreadToStop) {
                        log.error("bortus, JobScheduleHelper#ringThread error:{}", e);
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                } catch (InterruptedException e) {
                    if (!ringThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info("bortus, JobScheduleHelper#ringThread stop");
        };
    }

    private void refreshNextValidTime(JobInfo jobInfo, Date fromTime) throws ParseException {
        Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0);
            jobInfo.setTriggerNextTime(0);
        }
    }

    private void pushTimeRing(int ringSecond, Long jobId) {
        List<Long> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<Long>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);

        log.debug("bortus, schedule push time-ring : " + ringSecond + " = " + Arrays.asList(ringItemData));
    }

    public void toStop() {
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1); // wait
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (int second : ringData.keySet()) {
                List<Long> tmpData = ringData.get(second);
                if (tmpData != null && tmpData.size() > 0) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        this.jobTriggerPool.stop();

        log.info("bortus, JobScheduleHelper stop");
    }
}
