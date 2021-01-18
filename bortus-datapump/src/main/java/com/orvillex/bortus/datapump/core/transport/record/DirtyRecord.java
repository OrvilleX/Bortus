package com.orvillex.bortus.datapump.core.transport.record;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.Column;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

public class DirtyRecord implements Record {
    private List<Column> columns = new ArrayList<Column>();

    public static DirtyRecord asDirtyRecord(final Record record) {
        DirtyRecord result = new DirtyRecord();
        for (int i = 0; i < record.getColumnNumber(); i++) {
            result.addColumn(record.getColumn(i));
        }
        return result;
    }

    @Override
    public void addColumn(Column column) {
        this.columns.add(DirtyColumn.asDirtyColumn(column, this.columns.size()));
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this.columns);
    }

    @Override
    public void setColumn(int i, Column column) {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public Column getColumn(int i) {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public int getColumnNumber() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public int getByteSize() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public int getMemorySize() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}

class DirtyColumn extends Column {
    private int index;

    public static Column asDirtyColumn(final Column column, int index) {
        return new DirtyColumn(column, index);
    }

    private DirtyColumn(Column column, int index) {
        this(null == column ? null : column.getRawData(), null == column ? Column.Type.NULL : column.getType(),
                null == column ? 0 : column.getByteSize(), index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public Long asLong() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public Double asDouble() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public String asString() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public Date asDate() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public byte[] asBytes() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public Boolean asBoolean() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new DataPumpException(I18nUtil.getString("RUNTIME_ERROR") + "该方法不支持");
    }

    private DirtyColumn(Object object, Type type, int byteSize, int index) {
        super(object, type, byteSize);
        this.setIndex(index);
    }
}