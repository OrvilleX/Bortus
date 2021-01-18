package com.orvillex.bortus.datapump.core.task;

import com.orvillex.bortus.datapump.core.element.RecordReceiver;

/**
 * 写入器需实现基础类
 */
public abstract class WriterTask extends AbstractTask {
    public abstract void startWrite(RecordReceiver lineReceiver);

    public boolean supportFailOver() {
        return false;
    }
}
