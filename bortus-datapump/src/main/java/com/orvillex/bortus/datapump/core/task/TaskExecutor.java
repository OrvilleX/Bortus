package com.orvillex.bortus.datapump.core.task;

import com.orvillex.bortus.datapump.core.collector.LogFilePluginCollector;
import com.orvillex.bortus.datapump.core.collector.TaskCollector;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.enums.State;
import com.orvillex.bortus.datapump.core.enums.TaskType;
import com.orvillex.bortus.datapump.core.runner.AbstractRunner;
import com.orvillex.bortus.datapump.core.runner.ReaderRunner;
import com.orvillex.bortus.datapump.core.runner.WriterRunner;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.transport.channel.Channel;
import com.orvillex.bortus.datapump.core.transport.exchanger.BufferedRecordExchanger;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

import org.apache.commons.lang3.Validate;

public class TaskExecutor {
    private Channel channel;
    private Thread readerThread;
    private Thread writerThread;
    private ReaderRunner readerRunner;
    private WriterRunner writerRunner;
    private Communication taskCommunication;
    private int attemptCount = 1;
    private String appName;

    public TaskExecutor(Channel channel, ReaderTask readerTask, WriterTask writerTask, String appName, Communication communication) {
        this.appName = appName;
        this.taskCommunication = communication;
        Validate.notNull(this.taskCommunication, String.format("Communication没有注册过"));
        this.channel = channel;

        writerRunner = (WriterRunner) generateRunner(TaskType.WRITER, writerTask);
        this.writerThread = new Thread(writerRunner, String.format("%s--writer", appName));

        readerRunner = (ReaderRunner) generateRunner(TaskType.READER, readerTask);
        this.readerThread = new Thread(readerRunner, String.format("%s--reader", appName));
    }

    public void doStart() {
        this.writerThread.start();

        if (!this.writerThread.isAlive() || this.taskCommunication.getState() == State.FAILED) {
            throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR"), this.taskCommunication.getThrowable());
        }

        this.readerThread.start();
        if (!this.readerThread.isAlive() && this.taskCommunication.getState() == State.FAILED) {
            throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR"), this.taskCommunication.getThrowable());
        }
    }

    private AbstractRunner generateRunner(TaskType taskType, AbstractTask task) {
        AbstractRunner newRunner = null;
        TaskCollector pluginCollector;

        switch (taskType) {
            case READER:
                newRunner = new ReaderRunner(task);
                newRunner.setTriggerParam(task.getTriggerParam());
                pluginCollector = new LogFilePluginCollector(taskCommunication, taskType);
                RecordSender recordSender = new BufferedRecordExchanger(this.channel, pluginCollector);
                ((ReaderRunner) newRunner).setRecordSender(recordSender);
                task.setTaskCollector(pluginCollector);
                break;
            case WRITER:
                newRunner = new WriterRunner(task);
                newRunner.setTriggerParam(task.getTriggerParam());
                pluginCollector = new LogFilePluginCollector(taskCommunication, taskType);
                ((WriterRunner) newRunner)
                        .setRecordReceiver(new BufferedRecordExchanger(this.channel, pluginCollector));
                task.setTaskCollector(pluginCollector);
                break;
            default:
                throw new DataPumpException(
                        I18nUtil.getString("ARGUMENT_ERROR") + "Cant generateRunner for:" + taskType);
        }
        newRunner.setAppName(appName);
        newRunner.setRunnerCommunication(this.taskCommunication);

        return newRunner;
    }

    public boolean isTaskFinished() {
        if (readerThread.isAlive() || writerThread.isAlive()) {
            return false;
        }
        if (taskCommunication == null || !taskCommunication.isFinished()) {
            return false;
        }
        return true;
    }

    public long getTimeStamp() {
        return taskCommunication.getTimestamp();
    }

    public boolean supportFailOver() {
        return writerRunner.supportFailOver();
    }

    public int getAttemptCount() {
        return this.attemptCount;
    }

    public void setAttemptCount(int count) {
        this.attemptCount = count;
    }

    public void shutdown() {
        writerRunner.shutdown();
        readerRunner.shutdown();
        if (writerThread.isAlive()) {
            writerThread.interrupt();
        }
        if (readerThread.isAlive()) {
            readerThread.interrupt();
        }
    }

    public boolean isShutdown() {
        return !readerThread.isAlive() && !writerThread.isAlive();
    }
}
