package com.orvillex.bortus.manager.config.scheduler;

import lombok.Data;

/**
 * 任务调度配置
 * @author y-z-f
 * @version 0.1
 */
@Data
public class SchedulerProperties {
    private String accessToken;
    private int triggerPoolFastMax;
    private int triggerPoolSlowMax;
    private int logretentiondays;
}
