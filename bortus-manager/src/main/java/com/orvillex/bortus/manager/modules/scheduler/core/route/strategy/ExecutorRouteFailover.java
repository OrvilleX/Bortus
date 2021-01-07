package com.orvillex.bortus.manager.modules.scheduler.core.route.strategy;

import java.util.List;

import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorRouter;
import com.orvillex.bortus.manager.utils.I18nUtil;
import com.orvillex.bortus.manager.utils.JobSchedulerUtils;

/**
 * 基于故障转移算法的任务调度
 * 
 * @author y-z-f
 * @version 0.1
 */
public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {

            ReturnT<String> beatResult = null;
            try {
                ExecutorBiz executorBiz = JobSchedulerUtils.getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<String>(ReturnT.FAIL_CODE, "" + e);
            }
            beatResultSB.append((beatResultSB.length() > 0) ? "<br><br>" : "")
                    .append(I18nUtil.getString("jobconf_beat") + "：").append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode()).append("<br>msg：").append(beatResult.getMsg());

            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {

                beatResult.setMsg(beatResultSB.toString());
                beatResult.setContent(address);
                return beatResult;
            }
        }
        return new ReturnT<String>(ReturnT.FAIL_CODE, beatResultSB.toString());
    }
}
