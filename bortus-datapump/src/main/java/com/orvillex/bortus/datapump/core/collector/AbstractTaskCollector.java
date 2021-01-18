package com.orvillex.bortus.datapump.core.collector;

import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.enums.TaskType;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.statistics.CommunicationTool;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;
import com.orvillex.bortus.job.log.JobLogger;

public class AbstractTaskCollector extends TaskCollector {
    private Communication communication;
    private TaskType taskType;

    public AbstractTaskCollector(Communication communication, TaskType taskType) {
        this.communication = communication;
        this.taskType = taskType;
    }

    public Communication getCommunication() {
        return communication;
    }

    public TaskType getTaskType() {
        return this.taskType;
    }

    @Override
    final public void collectMessage(String key, String value) {
        this.communication.addMessage(key, value);
    }

    @Override
    public void collectDirtyRecord(Record dirtyRecord, Throwable t, String errorMessage) {
        if (null == dirtyRecord) {
            JobLogger.log("脏数据record=null.");
            return;
        }

        if (this.taskType.equals(taskType.READER)) {
            this.communication.increaseCounter(CommunicationTool.READ_FAILED_RECORDS, 1);
            this.communication.increaseCounter(CommunicationTool.READ_FAILED_BYTES, dirtyRecord.getByteSize());
        } else if (this.taskType.equals(taskType.WRITER)) {
            this.communication.increaseCounter(CommunicationTool.WRITE_FAILED_RECORDS, 1);
            this.communication.increaseCounter(CommunicationTool.WRITE_FAILED_BYTES, dirtyRecord.getByteSize());
        } else {
            throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + this.taskType);
        }
    }
}
