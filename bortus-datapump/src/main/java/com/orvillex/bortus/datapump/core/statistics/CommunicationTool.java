package com.orvillex.bortus.datapump.core.statistics;

public final class CommunicationTool {
    public static final String STAGE = "stage";
    public static final String READ_SUCCEED_RECORDS = "readSucceedRecords";
    public static final String READ_FAILED_RECORDS = "readFailedRecords";
    public static final String READ_SUCCEED_BYTES = "readSucceedBytes";
    public static final String READ_FAILED_BYTES = "readFailedBytes";
    public static final String WAIT_WRITER_TIME = "waitWriterTime";
    public static final String TRANSFORMER_USED_TIME = "totalTransformerUsedTime";
    public static final String WAIT_READER_TIME = "waitReaderTime";
    public static final String WRITE_RECEIVED_RECORDS = "writeReceivedRecords";
    public static final String WRITE_RECEIVED_BYTES = "writeReceivedBytes";
    public static final String WRITE_FAILED_RECORDS = "writeFailedRecords";
    public static final String WRITE_FAILED_BYTES = "writeFailedBytes";

    public static long getTotalReadRecords(final Communication communication) {
        return communication.getLongCounter(READ_SUCCEED_RECORDS) +
                communication.getLongCounter(READ_FAILED_RECORDS);
    }

    
    public static long getTotalReadBytes(final Communication communication) {
        return communication.getLongCounter(READ_SUCCEED_BYTES) +
                communication.getLongCounter(READ_FAILED_BYTES);
    }
}
