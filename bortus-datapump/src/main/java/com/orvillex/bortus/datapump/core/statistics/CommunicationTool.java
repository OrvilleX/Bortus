package com.orvillex.bortus.datapump.core.statistics;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.orvillex.bortus.datapump.utils.StrUtil;

import org.apache.commons.lang3.Validate;

public final class CommunicationTool {
    private static Metrics metrics = new Metrics();
    public static int count = 0;
    public static final String BYTE_SPEED = "byteSpeed";
    public static final String RECORD_SPEED = "recordSpeed";
    public static final String PERCENTAGE = "percentage";
    public static final String READ_SUCCEED_RECORDS = "readSucceedRecords";
    public static final String READ_FAILED_RECORDS = "readFailedRecords";
    public static final String READ_SUCCEED_BYTES = "readSucceedBytes";
    public static final String READ_FAILED_BYTES = "readFailedBytes";
    public static final String WAIT_WRITER_TIME = "waitWriterTime";
    public static final String WAIT_READER_TIME = "waitReaderTime";
    public static final String WRITE_RECEIVED_RECORDS = "writeReceivedRecords";
    public static final String WRITE_RECEIVED_BYTES = "writeReceivedBytes";
    public static final String WRITE_FAILED_RECORDS = "writeFailedRecords";
    public static final String WRITE_FAILED_BYTES = "writeFailedBytes";
    public static final String TOTAL_READ_RECORDS = "totalReadRecords";
    public static final String TOTAL_READ_BYTES = "totalReadBytes";
    public static final String TOTAL_ERROR_RECORDS = "totalErrorRecords";
    public static final String TOTAL_ERROR_BYTES = "totalErrorBytes";
    public static final String WRITE_SUCCEED_RECORDS = "writeSucceedRecords";
    public static final String WRITE_SUCCEED_BYTES = "writeSucceedBytes";

    public static long getTotalReadRecords(final Communication communication) {
        return communication.getLongCounter(READ_SUCCEED_RECORDS) + communication.getLongCounter(READ_FAILED_RECORDS);
    }

    public static long getTotalReadBytes(final Communication communication) {
        return communication.getLongCounter(READ_SUCCEED_BYTES) + communication.getLongCounter(READ_FAILED_BYTES);
    }

    public static Communication getReportCommunication(Communication now, Communication old) {
        Validate.isTrue(now != null && old != null, "为汇报准备的新旧metric不能为null");
        long totalReadRecords = getTotalReadRecords(now);
        long totalReadBytes = getTotalReadBytes(now);
        now.setLongCounter(TOTAL_READ_RECORDS, totalReadRecords);
        now.setLongCounter(TOTAL_READ_BYTES, totalReadBytes);
        now.setLongCounter(TOTAL_ERROR_RECORDS, getTotalErrorRecords(now));
        now.setLongCounter(TOTAL_ERROR_BYTES, getTotalErrorBytes(now));
        now.setLongCounter(WRITE_SUCCEED_RECORDS, getWriteSucceedRecords(now));
        now.setLongCounter(WRITE_SUCCEED_BYTES, getWriteSucceedBytes(now));
        long timeInterval = now.getTimestamp() - old.getTimestamp();
        long sec = timeInterval <= 1000 ? 1 : timeInterval / 1000;
        long bytesSpeed = (totalReadBytes - getTotalReadBytes(old)) / sec;
        long recordsSpeed = (totalReadRecords - getTotalReadRecords(old)) / sec;
        now.setLongCounter(BYTE_SPEED, bytesSpeed < 0 ? 0 : bytesSpeed);
        now.setLongCounter(RECORD_SPEED, recordsSpeed < 0 ? 0 : recordsSpeed);

        if (old.getThrowable() != null) {
            now.setThrowable(old.getThrowable());
        }
        return now;
    }

    public static long getTotalErrorRecords(final Communication communication) {
        return communication.getLongCounter(READ_FAILED_RECORDS) + communication.getLongCounter(WRITE_FAILED_RECORDS);
    }

    public static long getTotalErrorBytes(final Communication communication) {
        return communication.getLongCounter(READ_FAILED_BYTES) + communication.getLongCounter(WRITE_FAILED_BYTES);
    }

    public static long getWriteSucceedRecords(final Communication communication) {
        return communication.getLongCounter(WRITE_RECEIVED_RECORDS)
                - communication.getLongCounter(WRITE_FAILED_RECORDS);
    }

    public static long getWriteSucceedBytes(final Communication communication) {
        return communication.getLongCounter(WRITE_RECEIVED_BYTES) - communication.getLongCounter(WRITE_FAILED_BYTES);
    }

    public static class Stringify {
        private final static DecimalFormat df = new DecimalFormat("0.00");

        public static String getSnapshot(final Communication communication) {
            StringBuilder sb = new StringBuilder();
            sb.append("Total ");
            sb.append(getTotal(communication));
            sb.append(" | ");
            sb.append("Speed ");
            sb.append(getSpeed(communication));
            sb.append(" | ");
            sb.append("Error ");
            sb.append(getError(communication));
            sb.append(" | ");
            sb.append(" All Task WaitWriterTime ");
            sb.append(PerfTrace.unitTime(communication.getLongCounter(WAIT_WRITER_TIME)));
            sb.append(" | ");
            sb.append(" All Task WaitReaderTime ");
            sb.append(PerfTrace.unitTime(communication.getLongCounter(WAIT_READER_TIME)));
            sb.append(" | ");
            try {
                String speed = getSpeed(communication).split("KB/s")[0];
                System.out.println("GG:" + speed);
                if (speed.equals("0B/s, 0 records/s")) {
                    speed = "0";
                }
                if (count == 0) {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    ObjectName dataxSpeedName = new ObjectName("bortus.datapump.speed:type=RecordSpeed");
                    ObjectName dataxPercentageName = new ObjectName("bortus.datapump.percentage:type=Percentage");
                    ObjectName dataxRecordName = new ObjectName("bortus.datapump.record:type=WriteSpeed");
                    server.registerMBean(metrics, dataxSpeedName);
                    server.registerMBean(metrics, dataxPercentageName);
                    server.registerMBean(metrics, dataxRecordName);

                    count++;
                }
                metrics.setWriteSpeed(speed);
                metrics.setPercentage(communication.getDoubleCounter(PERCENTAGE) * 100 + "");
                metrics.setRecordSpeed(communication.getLongCounter(RECORD_SPEED) + "");

                System.out.println("start.....");
            } catch (Exception e) {
                System.out.println("自己代码出错了");
            }

            sb.append("Percentage ");
            sb.append(getPercentage(communication));
            return sb.toString();
        }

        private static String getTotal(final Communication communication) {
            return String.format("%d records, %d bytes", communication.getLongCounter(TOTAL_READ_RECORDS),
                    communication.getLongCounter(TOTAL_READ_BYTES));
        }

        public static String getSpeed(final Communication communication) {
            return String.format("%s/s, %d records/s", StrUtil.stringify(communication.getLongCounter(BYTE_SPEED)),
                    communication.getLongCounter(RECORD_SPEED));
        }

        private static String getError(final Communication communication) {
            return String.format("%d records, %d bytes", communication.getLongCounter(TOTAL_ERROR_RECORDS),
                    communication.getLongCounter(TOTAL_ERROR_BYTES));
        }

        private static String getPercentage(final Communication communication) {
            return df.format(communication.getDoubleCounter(PERCENTAGE) * 100) + "%";
        }
    }
}
