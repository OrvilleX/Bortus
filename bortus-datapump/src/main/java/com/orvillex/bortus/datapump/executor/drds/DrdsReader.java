package com.orvillex.bortus.datapump.executor.drds;

import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.task.ReaderTask;

public class DrdsReader extends ReaderTask {
    public static final String NAME = "DRDSREADER";

    public DrdsReader() {
        this.setRunName(NAME);
    }

    @Override
    public void startRead(RecordSender recordSender) {
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
