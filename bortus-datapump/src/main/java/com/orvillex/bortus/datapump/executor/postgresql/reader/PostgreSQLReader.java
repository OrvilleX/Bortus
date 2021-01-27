package com.orvillex.bortus.datapump.executor.postgresql.reader;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.datapump.executor.core.reader.CommonReaderTask;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;

/**
 * PostgreSQL读取
 * @author y-z-f
 * @version 0.1
 */
public class PostgreSQLReader extends ReaderTask {
    private static final DataBaseType DATABASE_TYPE = DataBaseType.PostgreSQL;

    private ReaderParam readerConfig;
    private CommonReaderTask commonReaderSlave;

    @Override
    public void startRead(RecordSender recordSender) {
        int fetchSize = this.readerConfig.getFetchSize();
        this.commonReaderSlave.startRead(this.readerConfig, recordSender, super.getTaskCollector(), fetchSize);
    }

    @Override
    public void init() {
        this.readerConfig = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        this.commonReaderSlave = new CommonReaderTask(DATABASE_TYPE);
        this.commonReaderSlave.init(this.readerConfig);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

}
