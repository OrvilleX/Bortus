package com.orvillex.bortus.datapump.executor.postgresql.writer;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;
import com.orvillex.bortus.datapump.executor.core.writer.CommonWriterTask;

/**
 * PostgreSQL写入
 * @author y-z-f
 * @version 0.1
 */
public class PostgreSQLWriter extends WriterTask {
    private static final DataBaseType DATABASE_TYPE = DataBaseType.PostgreSQL;

    private WriterParam writerConfig;
    private CommonWriterTask commomWriterSlave;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        this.commomWriterSlave.startWrite(lineReceiver, this.writerConfig, super.getTaskCollector());
    }

    @Override
    public void init() {
        this.writerConfig = JSON.parseObject(this.getTriggerParam(), WriterParam.class);
        this.commomWriterSlave = new CommonWriterTask(DATABASE_TYPE) {
            @Override
            public String calcValueHolder(String columnType) {
                if ("serial".equalsIgnoreCase(columnType)) {
                    return "?::int";
                } else if ("bit".equalsIgnoreCase(columnType)) {
                    return "?::bit varying";
                }
                return "?::" + columnType;
            }
        };
        this.commomWriterSlave.init(this.writerConfig);
    }

    @Override
    public void destroy() {
    }
}
