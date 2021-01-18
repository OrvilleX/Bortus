package com.orvillex.bortus.datapump.core.element;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

import org.apache.commons.lang3.ArrayUtils;

public class BytesColumn extends Column {
    public BytesColumn() {
        this(null);
    }

    public BytesColumn(byte[] bytes) {
        super(ArrayUtils.clone(bytes), Column.Type.BYTES, null == bytes ? 0 : bytes.length);
    }

    @Override
    public byte[] asBytes() {
        if (null == this.getRawData()) {
            return null;
        }
        return (byte[]) this.getRawData();
    }

    @Override
    public String asString() {
        if (null == this.getRawData()) {
            return null;
        }
        try {
            return ColumnCast.bytes2String(this);
        } catch (Exception e) {
            throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT")
                    + String.format(":Bytes[%s]不能转为String .", this.toString()));
        }
    }

    @Override
    public Long asLong() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Bytes类型不能转为Long");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Bytes类型不能转为BigDecimal");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Bytes类型不能转为BigInteger");
    }

    @Override
    public Double asDouble() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + ":Bytes类型不能转为Long");
    }

    @Override
    public Date asDate() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + "Bytes类型不能转为Date");
    }

    @Override
    public Boolean asBoolean() {
        throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT") + "Bytes类型不能转为Boolean");
    }
}
