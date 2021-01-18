package com.orvillex.bortus.datapump.core.runner;

import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.statistics.CommunicationTool;
import com.orvillex.bortus.datapump.core.statistics.PerfRecord;
import com.orvillex.bortus.datapump.core.task.AbstractTask;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.Validate;

public class WriterRunner extends AbstractRunner implements Runnable {
    private RecordReceiver recordReceiver;

    public void setRecordReceiver(RecordReceiver receiver) {
        this.recordReceiver = receiver;
    }

    public WriterRunner(AbstractTask abstractTask) {
        super(abstractTask);
    }

    @Override
    public void run() {
        Validate.isTrue(this.recordReceiver != null);
        WriterTask taskWriter = (WriterTask) this.getTask();
        PerfRecord channelWaitRead = new PerfRecord(this.getAppName(), Phase.WAIT_READ_TIME);
        try {
            channelWaitRead.start();
            JobLogger.log("task writer starts to do init ...");
            PerfRecord initPerfRecord = new PerfRecord(this.getAppName(), Phase.WRITE_TASK_INIT);
            initPerfRecord.start();
            taskWriter.init();
            initPerfRecord.end();

            JobLogger.log("task writer starts to do prepare ...");
            PerfRecord preparePerfRecord = new PerfRecord(this.getAppName(), Phase.WRITE_TASK_PREPARE);
            preparePerfRecord.start();
            taskWriter.prepare();
            preparePerfRecord.end();
            JobLogger.log("task writer starts to write ...");

            PerfRecord dataPerfRecord = new PerfRecord(this.getAppName(), Phase.WRITE_TASK_DATA);
            dataPerfRecord.start();
            taskWriter.startWrite(recordReceiver);

            dataPerfRecord.addCount(CommunicationTool.getTotalReadRecords(super.getRunnerCommunication()));
            dataPerfRecord.addSize(CommunicationTool.getTotalReadBytes(super.getRunnerCommunication()));
            dataPerfRecord.end();

            JobLogger.log("task writer starts to do post ...");
            PerfRecord postPerfRecord = new PerfRecord(this.getAppName(), Phase.WRITE_TASK_POST);
            postPerfRecord.start();
            taskWriter.post();
            postPerfRecord.end();

            super.markSuccess();
        } catch (Throwable e) {
            JobLogger.log("Writer Runner Received Exceptions:");
            JobLogger.log(e);
            super.markFail(e);
        } finally {
            JobLogger.log("task writer starts to do destroy ...");
            PerfRecord desPerfRecord = new PerfRecord(this.getAppName(), Phase.WRITE_TASK_DESTROY);
            desPerfRecord.start();
            super.destroy();
            desPerfRecord.end();
            channelWaitRead.end(super.getRunnerCommunication().getLongCounter(CommunicationTool.WAIT_READER_TIME));
        }
    }

    public boolean supportFailOver() {
        WriterTask taskWriter = (WriterTask) this.getTask();
        return taskWriter.supportFailOver();
    }

    public void shutdown() {
        recordReceiver.shutdown();
    }
}
