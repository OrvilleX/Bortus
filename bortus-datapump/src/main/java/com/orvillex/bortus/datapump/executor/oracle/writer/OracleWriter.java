package com.orvillex.bortus.datapump.executor.oracle.writer;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;
import com.orvillex.bortus.datapump.executor.core.writer.CommonWriterTask;

public class OracleWriter extends WriterTask {
    private static final DataBaseType DATABASE_TYPE = DataBaseType.Oracle;

    private WriterParam writerConfig;
    private CommonWriterTask commonWriterTask;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        this.commonWriterTask.startWrite(lineReceiver, this.writerConfig, super.getTaskCollector());
    }

    @Override
    public void init() {
        this.writerConfig = JSON.parseObject(this.getTriggerParam(), WriterParam.class);
        this.commonWriterTask = new CommonWriterTask(DATABASE_TYPE);
        this.commonWriterTask.init(this.writerConfig);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
