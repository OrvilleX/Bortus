package com.orvillex.bortus.manager.modules.scheduler.core.route.strategy;

import java.util.List;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorRouter;

/**
 * 基于最后一个算法的任务调度
 * @author y-z-f
 * @version 0.1
 */
public class ExecutorRouteLast extends ExecutorRouter {
    
    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT<String>(addressList.get(addressList.size()-1));
    }
}
