package com.orvillex.bortus.datapump.core.task;

import com.orvillex.bortus.datapump.core.collector.TaskCollector;

import lombok.Data;

@Data
public abstract class AbstractTask {
    /**
     * 标识当前操作的名称，如Read或Write
     */
    private String runName;
    private TaskCollector taskCollector;
    private String triggerParam;

    public abstract void init();

	public abstract void destroy();

    public void preCheck() {
    }

    public void prepare() {
    }

    public void post() {
    }

    public void preHandler(String jobConfiguration) {
    }

    public void postHandler(String jobConfiguration) {
    }
}
