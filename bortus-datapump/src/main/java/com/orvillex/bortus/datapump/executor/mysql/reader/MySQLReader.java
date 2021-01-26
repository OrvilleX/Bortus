package com.orvillex.bortus.datapump.executor.mysql.reader;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.datapump.executor.core.reader.CommonReaderTask;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;

public class MySQLReader extends ReaderTask {
    private static final DataBaseType DATABASE_TYPE = DataBaseType.MySql;
    private ReaderParam readerConfig;
    private CommonReaderTask commonReaderTask;

    @Override
    public void startRead(RecordSender recordSender) {
        int fetchSize = this.readerConfig.getFetchSize();
        this.commonReaderTask.startRead(this.readerConfig, recordSender, super.getTaskCollector(), fetchSize);
    }

    @Override
    public void init() {
        this.readerConfig = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        this.commonReaderTask = new CommonReaderTask(DATABASE_TYPE);
        this.commonReaderTask.init(this.readerConfig);
    }

    @Override
    public void destroy() {
    }
}
