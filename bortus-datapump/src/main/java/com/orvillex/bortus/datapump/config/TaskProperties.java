package com.orvillex.bortus.datapump.config;

import lombok.Data;

@Data
public class TaskProperties {
    private int failoverMaxRetryTimes = 1;
    private long failoverRetryInterval = 10000;
    private long failoverMaxWait = 60000;
    private int sleepInterval = 100;
    private long reportInterval = 10000;
}
