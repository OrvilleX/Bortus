package com.orvillex.bortus.datapump.core.element;

public interface RecordReceiver {

	public Record getFromReader();

	public void shutdown();
}
