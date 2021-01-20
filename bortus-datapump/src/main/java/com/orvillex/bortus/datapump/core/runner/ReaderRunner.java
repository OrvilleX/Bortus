package com.orvillex.bortus.datapump.core.runner;

import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.statistics.CommunicationTool;
import com.orvillex.bortus.datapump.core.statistics.PerfRecord;
import com.orvillex.bortus.datapump.core.task.AbstractTask;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.job.log.JobLogger;

public class ReaderRunner extends AbstractRunner implements Runnable {
    private RecordSender recordSender;

    public void setRecordSender(RecordSender recordSender) {
        this.recordSender = recordSender;
    }

    public ReaderRunner(AbstractTask abstractTask) {
        super(abstractTask);
    }

    @Override
    public void run() {
        assert null != this.recordSender;
        ReaderTask taskReader = (ReaderTask) this.getTask();

        PerfRecord channelWaitWrite = new PerfRecord(this.getAppName(), Phase.WAIT_WRITE_TIME);
        try {
            channelWaitWrite.start();

            JobLogger.log("task reader starts to do init ...");
            PerfRecord initPerfRecord = new PerfRecord(this.getAppName(), Phase.READ_TASK_INIT);
            initPerfRecord.start();
            taskReader.init();
            initPerfRecord.end();

            JobLogger.log("task reader starts to do prepare ...");
            PerfRecord preparePerfRecord = new PerfRecord(this.getAppName(), Phase.READ_TASK_PREPARE);
            preparePerfRecord.start();
            taskReader.prepare();
            preparePerfRecord.end();

            JobLogger.log("task reader starts to read ...");
            PerfRecord dataPerfRecord = new PerfRecord(this.getAppName(), Phase.READ_TASK_DATA);
            dataPerfRecord.start();
            taskReader.startRead(recordSender);
            recordSender.terminate();

            dataPerfRecord.addCount(CommunicationTool.getTotalReadRecords(super.getRunnerCommunication()));
            dataPerfRecord.addSize(CommunicationTool.getTotalReadBytes(super.getRunnerCommunication()));
            dataPerfRecord.end();

            JobLogger.log("task reader starts to do post ...");
            PerfRecord postPerfRecord = new PerfRecord(this.getAppName(), Phase.READ_TASK_POST);
            postPerfRecord.start();
            taskReader.post();
            postPerfRecord.end();
        } catch (Throwable e) {
            JobLogger.log("Reader runner Received Exceptions:");
            JobLogger.log(e);
            super.markFail(e);
        } finally {
            JobLogger.log("task reader starts to do destroy ...");
            PerfRecord desPerfRecord = new PerfRecord(this.getAppName(), Phase.READ_TASK_DESTROY);
            desPerfRecord.start();
            super.destroy();
            desPerfRecord.end();
            channelWaitWrite.end(super.getRunnerCommunication().getLongCounter(CommunicationTool.WAIT_WRITER_TIME));
        }
    }

    public void shutdown() {
        recordSender.shutdown();
    }
}
