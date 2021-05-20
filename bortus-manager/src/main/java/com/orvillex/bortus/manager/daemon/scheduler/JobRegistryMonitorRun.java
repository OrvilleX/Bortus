package com.orvillex.bortus.manager.daemon.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.job.enums.RegistryConfig;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobRegistryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobRegistryMonitorRun implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorRun.class);

    private volatile boolean toStop = false;

    private final JobGroupService jobGroupService;
    private final JobRegistryService jobRegistryService;

    public JobRegistryMonitorRun(JobGroupService groupService, JobRegistryService registryService) {
        jobGroupService = groupService;
        jobRegistryService = registryService;
    }

    @Override
    public void run() {
        while (!toStop) {
            try {
                List<JobGroup> groupList = jobGroupService.findByAddressType(0);
                if (groupList != null && !groupList.isEmpty()) {

                    List<Integer> ids = jobRegistryService.findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
                    if (ids != null && ids.size() > 0) {
                        jobRegistryService.delete(new HashSet<>(ids));
                    }

                    HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
                    List<JobRegistry> list = jobRegistryService.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
                    if (list != null) {
                        for (JobRegistry item : list) {
                            if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                String appname = item.getRegistryKey();
                                List<String> registryList = appAddressMap.get(appname);
                                if (registryList == null) {
                                    registryList = new ArrayList<String>();
                                }

                                if (!registryList.contains(item.getRegistryValue())) {
                                    registryList.add(item.getRegistryValue());
                                }
                                appAddressMap.put(appname, registryList);
                            }
                        }
                    }

                    for (JobGroup group : groupList) {
                        List<String> registryList = appAddressMap.get(group.getAppName());
                        String addressListStr = null;
                        if (registryList != null && !registryList.isEmpty()) {
                            Collections.sort(registryList);
                            addressListStr = "";
                            for (String item : registryList) {
                                addressListStr += item + ",";
                            }
                            addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                        }
                        group.setAddressList(addressListStr);
                        jobGroupService.update(group);
                    }
                }
            } catch (Exception e) {
                if (!toStop) {
                    logger.error("bortus, job registry monitor thread error:{}", e);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
            } catch (InterruptedException e) {
                if (!toStop) {
                    logger.error("bortus, job registry monitor thread error:{}", e);
                }
            }
        }
        logger.info("bortus, job registry monitor thread stop");
    }

    public void toStop() {
        toStop = true;
    }
}
