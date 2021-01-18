package com.orvillex.bortus.datapump.core.transport.record;

import com.orvillex.bortus.datapump.core.element.Column;
import com.orvillex.bortus.datapump.core.element.Record;

public class TerminateRecord implements Record {
    private final static TerminateRecord SINGLE = new TerminateRecord();

    private TerminateRecord() {
    }

    public static TerminateRecord get() {
        return SINGLE;
    }

    @Override
    public void addColumn(Column column) {
    }

    @Override
    public Column getColumn(int i) {
        return null;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public int getByteSize() {
        return 0;
    }

    @Override
    public int getMemorySize() {
        return 0;
    }

    @Override
    public void setColumn(int i, Column column) {
        return;
    }
}
