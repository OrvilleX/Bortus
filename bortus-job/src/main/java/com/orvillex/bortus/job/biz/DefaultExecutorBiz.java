package com.orvillex.bortus.job.biz;

import java.util.Date;

import com.orvillex.bortus.job.biz.models.IdleBeatParam;
import com.orvillex.bortus.job.biz.models.KillParam;
import com.orvillex.bortus.job.biz.models.LogParam;
import com.orvillex.bortus.job.biz.models.LogResult;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.job.enums.ExecutorBlockStrategyEnum;
import com.orvillex.bortus.job.executor.JobExecutor;
import com.orvillex.bortus.job.glue.GlueFactory;
import com.orvillex.bortus.job.glue.GlueTypeEnum;
import com.orvillex.bortus.job.handler.GlueJobHandler;
import com.orvillex.bortus.job.handler.IJobHandler;
import com.orvillex.bortus.job.handler.ScriptJobHandler;
import com.orvillex.bortus.job.log.JobFileAppender;
import com.orvillex.bortus.job.thread.JobThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExecutorBiz implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(DefaultExecutorBiz.class);

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = JobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread!=null ? jobThread.getHandler():null;
        String removeOldReason = null;

        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        if (GlueTypeEnum.BEAN == glueTypeEnum) {
            IJobHandler newJobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

            if (jobThread!=null && jobHandler != newJobHandler) {
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {

            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                        && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                }
            }
        } else if (glueTypeEnum!=null && glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "block strategy effect："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }
        if (jobThread == null) {
            jobThread = JobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        JobThread jobThread = JobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            JobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.SUCCESS_CODE, "job thread already killed.");
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        String logFileName = JobFileAppender.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());

        LogResult logResult = JobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return new ReturnT<LogResult>(logResult);
    }
}
