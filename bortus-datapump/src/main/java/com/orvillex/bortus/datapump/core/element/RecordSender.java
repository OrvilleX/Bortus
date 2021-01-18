package com.orvillex.bortus.datapump.core.element;

public interface RecordSender {

	public Record createRecord();

	public void sendToWriter(Record record);

	public void flush();

	public void terminate();

	public void shutdown();
}
