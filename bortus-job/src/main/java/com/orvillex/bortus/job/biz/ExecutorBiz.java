package com.orvillex.bortus.job.biz;

import com.orvillex.bortus.job.biz.models.*;

/**
 * 执行接口
 */
public interface ExecutorBiz {
    
    /**
     * 心跳检测
     */
    public ReturnT<String> beat();

    /**
     * 忙碌检测
     */
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * 执行任务
     */
    public ReturnT<String> run(TriggerParam triggerParam);

    /**
     * 移除任务
     */
    public ReturnT<String> kill(KillParam killParam);

    /**
     * 任务日志
     */
    public ReturnT<LogResult> log(LogParam logParam);
}
