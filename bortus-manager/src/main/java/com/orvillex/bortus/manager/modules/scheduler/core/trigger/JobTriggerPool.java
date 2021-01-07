package com.orvillex.bortus.manager.modules.scheduler.core.trigger;

import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;

public interface JobTriggerPool {
    
    void trigger(Long jobId, TriggerTypeEnum triggerType, Integer failRetryCount, String executorShardingParam, String executorParam, String addressList);

    void start();

    void stop();
}
