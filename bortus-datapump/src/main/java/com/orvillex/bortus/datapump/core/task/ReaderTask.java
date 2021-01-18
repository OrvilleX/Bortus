package com.orvillex.bortus.datapump.core.task;

import com.orvillex.bortus.datapump.core.element.RecordSender;

/**
 * 读取器需实现的基础类
 */
public abstract class ReaderTask extends AbstractTask {
    public abstract void startRead(RecordSender recordSender);
}
