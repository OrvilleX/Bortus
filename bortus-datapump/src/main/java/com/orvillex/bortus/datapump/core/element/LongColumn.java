package com.orvillex.bortus.datapump.core.element;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

public class LongColumn extends Column {

    public LongColumn(final String data) {
        super(null, Column.Type.LONG, 0);
        if (null == data) {
            return;
        }
        try {
            BigInteger rawData = NumberUtils.createBigDecimal(data).toBigInteger();
            super.setRawData(rawData);
            super.setByteSize(data.length());
        } catch (Exception e) {
            throw new DataPumpException(
                    I18nUtil.getString("CONVERT_NOT_SUPPORT") + String.format("String[%s]不能转为Long .", data));
        }
    }

    public LongColumn(Long data) {
        this(null == data ? (BigInteger) null : BigInteger.valueOf(data));
    }

    public LongColumn(Integer data) {
        this(null == data ? (BigInteger) null : BigInteger.valueOf(data));
    }

    public LongColumn(BigInteger data) {
        this(data, null == data ? 0 : 8);
    }

    private LongColumn(BigInteger data, int byteSize) {
        super(data, Column.Type.LONG, byteSize);
    }

    public LongColumn() {
        this((BigInteger) null);
    }

    @Override
    public BigInteger asBigInteger() {
        if (null == this.getRawData()) {
            return null;
        }
        return (BigInteger) this.getRawData();
    }

    @Override
    public Long asLong() {
        BigInteger rawData = (BigInteger) this.getRawData();
        if (null == rawData) {
            return null;
        }
        OverFlowUtil.validateLongNotOverFlow(rawData);
        return rawData.longValue();
    }

    @Override
    public Double asDouble() {
        if (null == this.getRawData()) {
            return null;
        }
        BigDecimal decimal = this.asBigDecimal();
        OverFlowUtil.validateDoubleNotOverFlow(decimal);
        return decimal.doubleValue();
    }

    @Override
    public Boolean asBoolean() {
        if (null == this.getRawData()) {
            return null;
        }
        return this.asBigInteger().compareTo(BigInteger.ZERO) != 0 ? true : false;
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (null == this.getRawData()) {
            return null;
        }
        return new BigDecimal(this.asBigInteger());
    }

    @Override
    public String asString() {
        if (null == this.getRawData()) {
            return null;
        }
        return ((BigInteger) this.getRawData()).toString();
    }

    @Override
    public Date asDate() {
        if (null == this.getRawData()) {
            return null;
        }
        return new Date(this.asLong());
    }

    @Override
    public byte[] asBytes() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Long类型不能转为Bytes");
    }
}
