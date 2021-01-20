package com.orvillex.bortus.datapump.executor.drds;

import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;

public class DrdsWriter extends WriterTask {
    public static final String NAME = "DRDSWRITER";

    public DrdsWriter() {
        this.setRunName(NAME);
    }

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        // TODO Auto-generated method stub

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
