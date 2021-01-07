package com.orvillex.bortus.manager.modules.scheduler.core.route.strategy;

import java.util.List;
import java.util.Random;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorRouter;

/**
 * 基于随机算法的任务调度
 * @author y-z-f
 * @version 0.1
 */
public class ExecutorRouteRandom extends ExecutorRouter {
    private static Random localRandom = new Random();

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(localRandom.nextInt(addressList.size()));
        return new ReturnT<String>(address);
    }
}
