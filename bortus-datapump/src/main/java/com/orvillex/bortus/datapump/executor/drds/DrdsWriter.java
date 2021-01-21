package com.orvillex.bortus.datapump.executor.drds;

import com.orvillex.bortus.datapump.core.element.Column;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.element.StringColumn;
import com.orvillex.bortus.datapump.core.element.Column.Type;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.job.log.JobLogger;

public class DrdsWriter extends WriterTask {
    public static final String NAME = "DRDSWRITER";

    public DrdsWriter() {
        this.setRunName(NAME);
    }

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        while (true) {
            Record record = lineReceiver.getFromReader();
            if (record == null) {
                break;
            }
            Column column = record.getColumn(0);
            if (column.getType() == Type.STRING) {
                StringColumn scolumn = (StringColumn)column;
            }
        }
        try {
            for(int i = 0; i < 5; i++) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            
        }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
    
}
