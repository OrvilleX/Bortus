package com.orvillex.bortus.datapump.core.element;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

public class DateColumn extends Column {
    private DateType subType = DateType.DATETIME;

    public static enum DateType {
        DATE, TIME, DATETIME
    }

    public DateColumn() {
        this((Long) null);
    }

    public DateColumn(final Long stamp) {
        super(stamp, Column.Type.DATE, (null == stamp ? 0 : 8));
    }

    public DateColumn(final Date date) {
        this(date == null ? null : date.getTime());
    }

    public DateColumn(final java.sql.Date date) {
        this(date == null ? null : date.getTime());
        this.setSubType(DateType.DATE);
    }

    public DateColumn(final java.sql.Time time) {
        this(time == null ? null : time.getTime());
        this.setSubType(DateType.TIME);
    }

    public DateColumn(final java.sql.Timestamp ts) {
        this(ts == null ? null : ts.getTime());
        this.setSubType(DateType.DATETIME);
    }

    @Override
    public Long asLong() {

        return (Long) this.getRawData();
    }

    @Override
    public String asString() {
        try {
            return ColumnCast.date2String(this);
        } catch (Exception e) {
            throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT")
                    + String.format(":Date[%s]类型不能转为String .", this.toString()));
        }
    }

    @Override
    public Date asDate() {
        if (null == this.getRawData()) {
            return null;
        }

        return new Date((Long) this.getRawData());
    }

    @Override
    public byte[] asBytes() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Date类型不能转为Bytes");
    }

    @Override
    public Boolean asBoolean() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Date类型不能转为Boolean");
    }

    @Override
    public Double asDouble() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Date类型不能转为Double");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Date类型不能转为BigInteger");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Date类型不能转为BigDecimal");
    }

    public DateType getSubType() {
        return subType;
    }

    public void setSubType(DateType subType) {
        this.subType = subType;
    }
}
