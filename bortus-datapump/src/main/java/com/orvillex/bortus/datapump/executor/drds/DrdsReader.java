package com.orvillex.bortus.datapump.executor.drds;

import java.util.Random;

import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.element.StringColumn;
import com.orvillex.bortus.datapump.core.task.ReaderTask;

public class DrdsReader extends ReaderTask {
    public static final String NAME = "DRDSREADER";

    public DrdsReader() {
        this.setRunName(NAME);
    }

    @Override
    public void startRead(RecordSender recordSender) {
        Random random = new Random();
        try {
        for(int i = 0; i < 100000; i++) {
            Record record = recordSender.createRecord();
            record.addColumn(new StringColumn("ele" + i));
            recordSender.sendToWriter(record);
            int sleepMS = random.nextInt(50);
            Thread.sleep(sleepMS);
        }
    } catch (Exception ex) {

    } finally {
        recordSender.flush();
        recordSender.terminate();
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
