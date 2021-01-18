package com.orvillex.bortus.datapump.core.element;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.orvillex.bortus.datapump.config.ColumnProperties;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.utils.I18nUtil;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * 自动列值转换工具类
 * 
 * @author y-z-f
 * @version 0.1
 */
public final class ColumnCast {

	public static void bind(final ColumnProperties configuration) {
		StringCast.init(configuration);
		DateCast.init(configuration);
		BytesCast.init(configuration);
	}

	public static Date string2Date(final StringColumn column) throws ParseException {
		return StringCast.asDate(column);
	}

	public static byte[] string2Bytes(final StringColumn column) throws UnsupportedEncodingException {
		return StringCast.asBytes(column);
	}

	public static String date2String(final DateColumn column) {
		return DateCast.asString(column);
	}

	public static String bytes2String(final BytesColumn column) throws UnsupportedEncodingException {
		return BytesCast.asString(column);
	}
}

class StringCast {
	static String datetimeFormat;
	static String dateFormat;
	static String timeFormat;
	static List<String> extraFormats = Collections.emptyList();
	static String timeZone;
	static FastDateFormat dateFormatter;
	static FastDateFormat timeFormatter;
	static FastDateFormat datetimeFormatter;
	static TimeZone timeZoner;
	static String encoding;

	static void init(final ColumnProperties config) {
		StringCast.datetimeFormat = config.getDatetimeFormat();
		StringCast.dateFormat = config.getDateFormat();
		StringCast.timeFormat = config.getTimeFormat();
		StringCast.extraFormats = config.getExtraFormats();
		StringCast.timeZone = config.getTimeZone();
		StringCast.timeZoner = TimeZone.getTimeZone(StringCast.timeZone);
		StringCast.encoding = config.getEncoding();

		StringCast.datetimeFormatter = FastDateFormat.getInstance(StringCast.datetimeFormat, StringCast.timeZoner);
		StringCast.dateFormatter = FastDateFormat.getInstance(StringCast.dateFormat, StringCast.timeZoner);
		StringCast.timeFormatter = FastDateFormat.getInstance(StringCast.timeFormat, StringCast.timeZoner);
	}

	static Date asDate(final StringColumn column) throws ParseException {
		if (null == column.asString()) {
			return null;
		}
		try {
			return StringCast.datetimeFormatter.parse(column.asString());
		} catch (ParseException ignored) {
		}
		try {
			return StringCast.dateFormatter.parse(column.asString());
		} catch (ParseException ignored) {
		}
		ParseException e;
		try {
			return StringCast.timeFormatter.parse(column.asString());
		} catch (ParseException ignored) {
			e = ignored;
		}

		for (String format : StringCast.extraFormats) {
			try {
				return FastDateFormat.getInstance(format, StringCast.timeZoner).parse(column.asString());
			} catch (ParseException ignored) {
				e = ignored;
			}
		}
		throw e;
	}

	static byte[] asBytes(final StringColumn column) throws UnsupportedEncodingException {
		if (null == column.asString()) {
			return null;
		}

		return column.asString().getBytes(StringCast.encoding);
	}
}

class DateCast {

	static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

	static String dateFormat = "yyyy-MM-dd";

	static String timeFormat = "HH:mm:ss";

	static String timeZone = "GMT+8";

	static TimeZone timeZoner = TimeZone.getTimeZone(DateCast.timeZone);

	static void init(final ColumnProperties config) {
		DateCast.datetimeFormat = config.getDatetimeFormat();
		DateCast.timeFormat = config.getTimeFormat();
		DateCast.dateFormat = config.getDateFormat();
		DateCast.timeZone = config.getTimeZone();
		DateCast.timeZoner = TimeZone.getTimeZone(DateCast.timeZone);
		return;
	}

	static String asString(final DateColumn column) {
		if (null == column.asDate()) {
			return null;
		}

		switch (column.getSubType()) {
			case DATE:
				return DateFormatUtils.format(column.asDate(), DateCast.dateFormat, DateCast.timeZoner);
			case TIME:
				return DateFormatUtils.format(column.asDate(), DateCast.timeFormat, DateCast.timeZoner);
			case DATETIME:
				return DateFormatUtils.format(column.asDate(), DateCast.datetimeFormat, DateCast.timeZoner);
			default:
				throw new DataPumpException(I18nUtil.getString("CONVERT_NOT_SUPPORT"));
		}
	}
}

class BytesCast {
	static String encoding = "utf-8";

	static void init(final ColumnProperties config) {
		BytesCast.encoding = config.getEncoding();
		return;
	}

	static String asString(final BytesColumn column) throws UnsupportedEncodingException {
		if (null == column.asBytes()) {
			return null;
		}
		return new String(column.asBytes(), encoding);
	}
}
