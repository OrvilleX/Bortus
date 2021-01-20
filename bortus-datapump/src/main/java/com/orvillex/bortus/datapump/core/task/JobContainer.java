package com.orvillex.bortus.datapump.core.task;

import com.orvillex.bortus.datapump.config.TaskProperties;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.enums.State;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.statistics.CommunicationTool;
import com.orvillex.bortus.datapump.core.statistics.PerfRecord;
import com.orvillex.bortus.datapump.core.statistics.PerfTrace;
import com.orvillex.bortus.datapump.core.transport.channel.Channel;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;
import com.orvillex.bortus.datapump.utils.VMInfo;
import com.orvillex.bortus.job.log.JobLogger;

public class JobContainer {
    private int sleepIntervalInMillSec = 100;
    private long reportIntervalInMillSec = 10000;
    private int taskMaxRetryTimes = 1;
    private long taskRetryIntervalInMsec = 10000;
    private long taskMaxWaitInMsec = 100;
    private Channel taskChannel;
    private long lastReportTime = System.currentTimeMillis();
    private VMInfo vmInfo = VMInfo.getVmInfo();
    private ReaderTask readerTask;
    private WriterTask writerTask;
    private String appName;

    public JobContainer(Channel channel, ReaderTask readerTask, WriterTask writerTask) {
        this.taskChannel = channel;
        this.readerTask = readerTask;
        this.writerTask = writerTask;
    }

    public void setTaskProperties(TaskProperties taskProperties) {
        this.sleepIntervalInMillSec = taskProperties.getSleepInterval();
        this.reportIntervalInMillSec = taskProperties.getReportInterval();
        this.taskMaxRetryTimes = taskProperties.getFailoverMaxRetryTimes();
        this.taskRetryIntervalInMsec = taskProperties.getFailoverRetryInterval();
        this.taskMaxWaitInMsec = taskProperties.getFailoverMaxWait();
    }

    public void start() {
        Communication lastTaskCommunication = new Communication();
        Communication nowTaskCommunication = new Communication();
        try {
            this.taskChannel.setCommunication(nowTaskCommunication);
            TaskExecutor taskExecutor = new TaskExecutor(this.taskChannel, this.readerTask, this.writerTask);
            Long taskStartTime = System.currentTimeMillis();
            long lastReportTimeStamp = 0;

            while (true) {
                boolean failedOrKilled = false;

                if (nowTaskCommunication.isFinished()) {
                    if (nowTaskCommunication.getState() == State.FAILED) {
                        if (taskExecutor.supportFailOver() && taskExecutor.getAttemptCount() < taskMaxRetryTimes) {
                            taskExecutor.shutdown();
                            nowTaskCommunication = new Communication();
                            this.taskChannel.setCommunication(nowTaskCommunication);
                            int attemptCount = taskExecutor.getAttemptCount() + 1;
                            long now = System.currentTimeMillis();
                            long failedTime = taskExecutor.getTimeStamp();
                            taskExecutor.setAttemptCount(attemptCount);
                            if (now - failedTime < taskRetryIntervalInMsec) {
                                continue;
                            }
                            if (!taskExecutor.isShutdown()) {
                                if (now - failedTime > taskMaxWaitInMsec) {
                                    nowTaskCommunication.setState(State.FAILED);
                                    reportTaskCommunication(nowTaskCommunication, lastTaskCommunication);
                                    throw new DataPumpException(
                                            I18nUtil.getString("WAIT_TIME_EXCEED") + "task failover等待超时");
                                } else {
                                    taskExecutor.shutdown();
                                    continue;
                                }
                            } else {
                                JobLogger.log("attemptCount[{}] has already shutdown", taskExecutor.getAttemptCount());
                            }
                        } else {
                            failedOrKilled = true;
                        }
                    } else if (nowTaskCommunication.getState() == State.KILLED) {
                        failedOrKilled = true;
                    } else if (nowTaskCommunication.getState() == State.SUCCEEDED) {
                        if (taskStartTime != null) {
                            Long usedTime = System.currentTimeMillis() - taskStartTime;
                            JobLogger.log("task is successed, used[{}]ms", usedTime);
                            PerfRecord.addPerfRecord(this.appName, Phase.TASK_TOTAL, taskStartTime,
                                    usedTime * 1000L * 1000L);
                        }
                    }
                }
                taskExecutor.doStart();
                JobLogger.log("{} attemptCount[{}] is started", this.appName, taskExecutor.getAttemptCount());
                if (taskExecutor.isTaskFinished() && nowTaskCommunication.getState() == State.SUCCEEDED) {
                    lastTaskCommunication = reportTaskCommunication(nowTaskCommunication, lastTaskCommunication);
                    JobLogger.log("{} completed it's tasks.", this.appName);
                    break;
                }
                if (failedOrKilled) {
                    lastTaskCommunication = reportTaskCommunication(nowTaskCommunication, lastTaskCommunication);
                    throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR"),
                            lastTaskCommunication.getThrowable());
                }
                long now = System.currentTimeMillis();
                if (now - lastReportTimeStamp > reportIntervalInMillSec) {
                    lastTaskCommunication = reportTaskCommunication(nowTaskCommunication, lastTaskCommunication);
                    lastReportTimeStamp = now;
                }
                Thread.sleep(sleepIntervalInMillSec);
            }
            reportTaskCommunication(nowTaskCommunication, lastTaskCommunication);
        } catch (Throwable e) {
            if (nowTaskCommunication.getThrowable() == null) {
                nowTaskCommunication.setThrowable(e);
            }
            nowTaskCommunication.setState(State.FAILED);
            reportTaskCommunication(nowTaskCommunication);
            throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR"), e);
        } finally {
            VMInfo vmInfo = VMInfo.getVmInfo();
            if (vmInfo != null) {
                vmInfo.getDelta(false);
                JobLogger.log(vmInfo.totalString());
            }
            JobLogger.log(PerfTrace.getInstance().summarizeNoException());
        }
    }

    private void reportTaskCommunication(Communication communication) {
        JobLogger.log(CommunicationTool.Stringify.getSnapshot(communication));
        reportVmInfo();
    }

    private Communication reportTaskCommunication(Communication now, Communication last) {
        now.setTimestamp(System.currentTimeMillis());
        Communication reportCommunication = CommunicationTool.getReportCommunication(now, last);
        JobLogger.log(CommunicationTool.Stringify.getSnapshot(reportCommunication));
        reportVmInfo();
        return reportCommunication;
    }

    public void reportVmInfo() {
        long now = System.currentTimeMillis();
        if (now - lastReportTime >= 300000) {
            if (vmInfo != null) {
                vmInfo.getDelta(true);
            }
            lastReportTime = now;
        }
    }
}
