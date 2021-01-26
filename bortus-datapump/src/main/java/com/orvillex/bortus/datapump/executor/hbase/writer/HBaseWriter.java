package com.orvillex.bortus.datapump.executor.hbase.writer;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;

/**
 * HBase写入
 * @author y-z-f
 * @version 0.1
 */
public class HBaseWriter extends WriterTask {
    private WriterParam taskConfig;
    private HBaseWriterTask writerTask;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        this.writerTask.startWriter(lineReceiver, super.getTaskCollector());
    }

    @Override
    public void init() {
        this.taskConfig = JSON.parseObject(this.getTriggerParam(), WriterParam.class);
        this.writerTask = new HBaseWriterTask(this.taskConfig);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
