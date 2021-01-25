package com.orvillex.bortus.datapump.executor.hbase.reader;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.task.ReaderTask;

public class HBaseReader extends ReaderTask {
    private ReaderParam readerConfig;
    private HBaseReaderTask hbaseReaderTask;

    @Override
    public void startRead(RecordSender recordSender) {
        hbaseReaderTask.readRecord(recordSender);
    }

    @Override
    public void init() {
        readerConfig = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        hbaseReaderTask = new HBaseReaderTask(readerConfig);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
