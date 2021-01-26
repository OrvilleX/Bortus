package com.orvillex.bortus.datapump.executor.mysql.writer;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;
import com.orvillex.bortus.datapump.executor.core.writer.CommonWriterTask;

public class MySQLWriter extends WriterTask {
    private static final DataBaseType DATABASE_TYPE = DataBaseType.MySql;
    
    private WriteParam writerConfig;
    private CommonWriterTask commonRdbmsWriterTask;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        this.commonRdbmsWriterTask.startWrite(lineReceiver, this.writerConfig, super.getTaskCollector());
    }

    @Override
    public void init() {
        this.writerConfig = JSON.parseObject(this.getTriggerParam(), WriteParam.class);
        this.commonRdbmsWriterTask = new CommonWriterTask(DATABASE_TYPE);
        this.commonRdbmsWriterTask.init(this.writerConfig);
    }

    @Override
    public void destroy() {
    }
    
}
