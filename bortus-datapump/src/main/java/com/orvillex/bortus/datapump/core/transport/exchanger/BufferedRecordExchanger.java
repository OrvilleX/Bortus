package com.orvillex.bortus.datapump.core.transport.exchanger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.orvillex.bortus.datapump.config.TransportProperties;
import com.orvillex.bortus.datapump.core.collector.TaskCollector;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.transport.channel.Channel;
import com.orvillex.bortus.datapump.core.transport.record.DefaultRecord;
import com.orvillex.bortus.datapump.core.transport.record.TerminateRecord;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

import org.apache.commons.lang3.Validate;

/**
 * 提供数据交换能力
 */
public class BufferedRecordExchanger implements RecordSender, RecordReceiver {
    private final Channel channel;
    private final List<Record> buffer;
    private int bufferSize;
    protected final int byteCapacity;
    private final AtomicInteger memoryBytes = new AtomicInteger(0);
    private int bufferIndex = 0;
    private volatile boolean shutdown = false;
    private final TaskCollector collector;
    private TransportProperties properties;

    public BufferedRecordExchanger(final Channel channel, final TaskCollector collector) {
        assert null != channel;

        this.channel = channel;
        this.collector = collector;
        this.properties = channel.getProperties();
        this.bufferSize = properties.getExchangerBufferSize();
        this.buffer = new ArrayList<Record>(bufferSize);
        this.byteCapacity = properties.getChannelCapacityByte();
    }

    @Override
    public Record createRecord() {
        return new DefaultRecord();
    }

    @Override
	public void sendToWriter(Record record) {
		if(shutdown){
            throw new DataPumpException(I18nUtil.getString("SHUT_DOWN_TASK"));
		}

		Validate.notNull(record, "record不能为空.");
		if (record.getMemorySize() > this.byteCapacity) {
			this.collector.collectDirtyRecord(record, new Exception(String.format("单条记录超过大小限制，当前限制为:%s", this.byteCapacity)));
			return;
		}

		boolean isFull = (this.bufferIndex >= this.bufferSize || this.memoryBytes.get() + record.getMemorySize() > this.byteCapacity);
		if (isFull) {
			flush();
		}

		this.buffer.add(record);
		this.bufferIndex++;
		memoryBytes.addAndGet(record.getMemorySize());
	}

    @Override
    public void flush() {
        if (shutdown) {
            throw new DataPumpException(I18nUtil.getString("SHUT_DOWN_TASK"));
        }
        this.channel.pushAll(this.buffer);
        this.buffer.clear();
        this.bufferIndex = 0;
        this.memoryBytes.set(0);
    }

    @Override
    public void terminate() {
        if (shutdown) {
            throw new DataPumpException(I18nUtil.getString("SHUT_DOWN_TASK"));
        }
        flush();
        this.channel.pushTerminate(TerminateRecord.get());
    }

    @Override
    public Record getFromReader() {
        if (shutdown) {
            throw new DataPumpException(I18nUtil.getString("SHUT_DOWN_TASK"));
        }
        boolean isEmpty = (this.bufferIndex >= this.buffer.size());
        if (isEmpty) {
            receive();
        }
        Record record = this.buffer.get(this.bufferIndex++);
        if (record instanceof TerminateRecord) {
            record = null;
        }
        return record;
    }

    @Override
    public void shutdown() {
        shutdown = true;
        try {
            buffer.clear();
            channel.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void receive() {
        this.channel.pullAll(this.buffer);
        this.bufferIndex = 0;
        this.bufferSize = this.buffer.size();
    }
}
