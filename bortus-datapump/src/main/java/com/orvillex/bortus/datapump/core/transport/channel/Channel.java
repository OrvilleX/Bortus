package com.orvillex.bortus.datapump.core.transport.channel;

import java.util.Collection;

import com.orvillex.bortus.datapump.config.TransportProperties;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.statistics.Communication;
import com.orvillex.bortus.datapump.core.statistics.CommunicationTool;
import com.orvillex.bortus.datapump.core.transport.record.TerminateRecord;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.Validate;

public abstract class Channel {
    protected int capacity;
    protected int byteCapacity;
    protected long byteSpeed;
    protected long recordSpeed;
    protected long flowControlInterval;
    protected volatile boolean isClosed = false;
    protected volatile long waitReaderTime = 0;
    protected volatile long waitWriterTime = 0;
    private static Boolean isFirstPrint = true;
    private Communication currentCommunication;
    private Communication lastCommunication = new Communication();
    private TransportProperties properties;

    public Channel(final TransportProperties properties) {
        this.properties = properties;
        int capacity = properties.getChannelCapacity();
        long byteSpeed = properties.getChannelSpeedByte();
        long recordSpeed = properties.getChannelSpeedRecord();
        if (capacity <= 0) {
            throw new IllegalArgumentException(String.format("通道容量[%d]必须大于0.", capacity));
        }

        synchronized (isFirstPrint) {
            if (isFirstPrint) {
                JobLogger.log("Channel set byte_speed_limit to " + byteSpeed
                        + (byteSpeed <= 0 ? ", No bps activated." : "."));
                JobLogger.log("Channel set record_speed_limit to " + recordSpeed
                        + (recordSpeed <= 0 ? ", No tps activated." : "."));
                isFirstPrint = false;
            }
        }

        this.capacity = capacity;
        this.byteSpeed = byteSpeed;
        this.recordSpeed = recordSpeed;
        this.flowControlInterval = properties.getChannelFlowControlInterval();
        this.byteCapacity = properties.getChannelCapacityByte();
    }

    public void close() {
        this.isClosed = true;
    }

    public void open() {
        this.isClosed = false;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getByteSpeed() {
        return byteSpeed;
    }

    public TransportProperties getProperties() {
        return this.properties;
    }

    public void setCommunication(final Communication communication) {
        this.currentCommunication = communication;
        this.lastCommunication.reset();
    }

    public void push(final Record r) {
        Validate.notNull(r, "record不能为空.");
        this.doPush(r);
        this.statPush(1L, r.getByteSize());
    }

    public void pushTerminate(final TerminateRecord r) {
        Validate.notNull(r, "record不能为空.");
        this.doPush(r);
    }

    public void pushAll(final Collection<Record> rs) {
        Validate.notNull(rs);
        Validate.noNullElements(rs);
        this.doPushAll(rs);
        this.statPush(rs.size(), this.getByteSize(rs));
    }

    public Record pull() {
        Record record = this.doPull();
        this.statPull(1L, record.getByteSize());
        return record;
    }

    public void pullAll(final Collection<Record> rs) {
        Validate.notNull(rs);
        this.doPullAll(rs);
        this.statPull(rs.size(), this.getByteSize(rs));
    }

    protected abstract void doPush(Record r);

    protected abstract void doPushAll(Collection<Record> rs);

    protected abstract Record doPull();

    protected abstract void doPullAll(Collection<Record> rs);

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract void clear();

    private long getByteSize(final Collection<Record> rs) {
        long size = 0;
        for (final Record each : rs) {
            size += each.getByteSize();
        }
        return size;
    }

    private void statPush(long recordSize, long byteSize) {
        currentCommunication.increaseCounter(CommunicationTool.READ_SUCCEED_RECORDS, recordSize);
        currentCommunication.increaseCounter(CommunicationTool.READ_SUCCEED_BYTES, byteSize);
        currentCommunication.setLongCounter(CommunicationTool.WAIT_READER_TIME, waitReaderTime);
        currentCommunication.setLongCounter(CommunicationTool.WAIT_WRITER_TIME, waitWriterTime);

        boolean isChannelByteSpeedLimit = (this.byteSpeed > 0);
        boolean isChannelRecordSpeedLimit = (this.recordSpeed > 0);
        if (!isChannelByteSpeedLimit && !isChannelRecordSpeedLimit) {
            return;
        }

        long lastTimestamp = lastCommunication.getTimestamp();
        long nowTimestamp = System.currentTimeMillis();
        long interval = nowTimestamp - lastTimestamp;
        if (interval - this.flowControlInterval >= 0) {
            long byteLimitSleepTime = 0;
            long recordLimitSleepTime = 0;
            if (isChannelByteSpeedLimit) {
                long currentByteSpeed = (CommunicationTool.getTotalReadBytes(currentCommunication)
                        - CommunicationTool.getTotalReadBytes(lastCommunication)) * 1000 / interval;
                if (currentByteSpeed > this.byteSpeed) {
                    // 计算根据byteLimit得到的休眠时间
                    byteLimitSleepTime = currentByteSpeed * interval / this.byteSpeed - interval;
                }
            }

            if (isChannelRecordSpeedLimit) {
                long currentRecordSpeed = (CommunicationTool.getTotalReadRecords(currentCommunication)
                        - CommunicationTool.getTotalReadRecords(lastCommunication)) * 1000 / interval;
                if (currentRecordSpeed > this.recordSpeed) {
                    // 计算根据recordLimit得到的休眠时间
                    recordLimitSleepTime = currentRecordSpeed * interval / this.recordSpeed - interval;
                }
            }

            // 休眠时间取较大值
            long sleepTime = byteLimitSleepTime < recordLimitSleepTime ? recordLimitSleepTime : byteLimitSleepTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            lastCommunication.setLongCounter(CommunicationTool.READ_SUCCEED_BYTES,
                    currentCommunication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES));
            lastCommunication.setLongCounter(CommunicationTool.READ_FAILED_BYTES,
                    currentCommunication.getLongCounter(CommunicationTool.READ_FAILED_BYTES));
            lastCommunication.setLongCounter(CommunicationTool.READ_SUCCEED_RECORDS,
                    currentCommunication.getLongCounter(CommunicationTool.READ_SUCCEED_RECORDS));
            lastCommunication.setLongCounter(CommunicationTool.READ_FAILED_RECORDS,
                    currentCommunication.getLongCounter(CommunicationTool.READ_FAILED_RECORDS));
            lastCommunication.setTimestamp(nowTimestamp);
        }
    }

    private void statPull(long recordSize, long byteSize) {
        currentCommunication.increaseCounter(CommunicationTool.WRITE_RECEIVED_RECORDS, recordSize);
        currentCommunication.increaseCounter(CommunicationTool.WRITE_RECEIVED_BYTES, byteSize);
    }
}
