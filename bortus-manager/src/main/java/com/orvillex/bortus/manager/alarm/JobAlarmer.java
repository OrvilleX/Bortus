package com.orvillex.bortus.manager.alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 告警服务提供器
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
@Component
public class JobAlarmer implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private List<JobAlarm> jobAlarmList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JobAlarm> serviceBeanMap = applicationContext.getBeansOfType(JobAlarm.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            jobAlarmList = new ArrayList<JobAlarm>(serviceBeanMap.values());
        }
    }

    /**
     * 任务告警，将使用所有可用的告警方式进行给告警
     */
    public boolean alarm(JobInfo info, JobLog jobLog) {
        boolean result = false;

        if (jobAlarmList!=null && jobAlarmList.size()>0) {
            result = true;
            for (JobAlarm alarm: jobAlarmList) {
                boolean resultItem = false;
                try {
                    resultItem = alarm.doAlarm(info, jobLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                if (!resultItem) {
                    result = false;
                }
            }
        }
        return result;
    }
}
