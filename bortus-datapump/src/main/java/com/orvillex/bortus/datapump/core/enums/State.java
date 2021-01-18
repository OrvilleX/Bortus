package com.orvillex.bortus.datapump.core.enums;

/**
 * 任务状态
 */
public enum State {
    SUBMITTING(10),
    WAITING(20),
    RUNNING(30),
    KILLING(40),
    KILLED(50),
    FAILED(60),
    SUCCEEDED(70);

    int value;

    State(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean isFinished() {
        return this == KILLED || this == FAILED || this == SUCCEEDED;
    }

    public boolean isRunning() {
        return !isFinished();
    }
}
