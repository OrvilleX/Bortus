package com.orvillex.bortus.datapump.core.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.enums.TaskType;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.transport.record.DirtyRecord;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;

public class LogFilePluginCollector extends AbstractTaskCollector {
    private static final int DEFAULT_MAX_DIRTYNUM = 128;
    private AtomicInteger maxLogNum = new AtomicInteger(0);
    private AtomicInteger currentLogNum = new AtomicInteger(0);

    public LogFilePluginCollector(Communication communication, TaskType type) {
        super(communication, type);
        maxLogNum = new AtomicInteger(DEFAULT_MAX_DIRTYNUM);
    }

    private String formatDirty(final Record dirty, final Throwable t, final String msg) {
        Map<String, Object> msgGroup = new HashMap<String, Object>();
        msgGroup.put("type", super.getTaskType().toString());
        if (StringUtils.isNotBlank(msg)) {
            msgGroup.put("message", msg);
        }
        if (null != t && StringUtils.isNotBlank(t.getMessage())) {
            msgGroup.put("exception", t.getMessage());
        }
        if (null != dirty) {
            msgGroup.put("record", DirtyRecord.asDirtyRecord(dirty).getColumns());
        }
        return JSON.toJSONString(msgGroup);
    }

    @Override
    public void collectDirtyRecord(Record dirtyRecord, Throwable t, String errorMessage) {
        int logNum = currentLogNum.getAndIncrement();
        if (logNum == 0 && t != null) {
            JobLogger.log(t);
        }
        if (maxLogNum.intValue() < 0 || currentLogNum.intValue() < maxLogNum.intValue()) {
            JobLogger.log("脏数据: \n" + this.formatDirty(dirtyRecord, t, errorMessage));
        }
        super.collectDirtyRecord(dirtyRecord, t, errorMessage);
    }
}
