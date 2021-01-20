package com.orvillex.bortus.datapump.core.runner;

import com.orvillex.bortus.datapump.core.enums.State;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.task.AbstractTask;

import org.apache.commons.lang3.Validate;

public abstract class AbstractRunner {
    private String appName;
    private AbstractTask task;
    private Communication runnerCommunication;
    private String triggerParam;

    public AbstractRunner(AbstractTask taskPlugin) {
        this.task = taskPlugin;
    }

    public void destroy() {
        if (this.task != null) {
            this.task.destroy();
        }
    }

    public State getRunnerState() {
        return this.runnerCommunication.getState();
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return this.appName;
    }

    public AbstractTask getTask() {
        return task;
    }

    public String getTriggerParam() {
        return this.triggerParam;
    }

    public void setTriggerParam(String triggerParam) {
        this.triggerParam = triggerParam;
        this.task.setTriggerParam(triggerParam);
    }

    private void mark(State state) {
        this.runnerCommunication.setState(state);
    }

    public void markRun() {
        mark(State.RUNNING);
    }

    public void markSuccess() {
        mark(State.SUCCEEDED);
    }

    public void markFail(final Throwable throwable) {
        mark(State.FAILED);
        this.runnerCommunication.setTimestamp(System.currentTimeMillis());
        this.runnerCommunication.setThrowable(throwable);
    }

    public void setRunnerCommunication(final Communication runnerCommunication) {
        Validate.notNull(runnerCommunication,
                "插件的Communication不能为空");
        this.runnerCommunication = runnerCommunication;
    }

    public Communication getRunnerCommunication() {
        return runnerCommunication;
    }

    public abstract void shutdown();
}
