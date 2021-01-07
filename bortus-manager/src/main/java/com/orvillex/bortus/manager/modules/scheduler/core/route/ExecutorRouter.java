package com.orvillex.bortus.manager.modules.scheduler.core.route;

import java.util.List;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据算法选择执行器节点
 */
public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * 计算任务路由
     */
    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);
}
