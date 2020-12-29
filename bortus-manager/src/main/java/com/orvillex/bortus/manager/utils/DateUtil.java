package com.orvillex.bortus.manager.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 * @author y-z-f
 * @version 0.1
 */
public class DateUtil {
    public static final DateTimeFormatter TIME_OF_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIME_OF_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 日期转时间戳
     */
    public static Long getTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 时间戳转日期
     */
    public static LocalDateTime fromTimeStamp(Long timeStamp) {
        return LocalDateTime.ofEpochSecond(timeStamp, 0, OffsetDateTime.now().getOffset());
    }

    /**
     * 日期转旧日期对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 日期转旧日期对象
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atTime(LocalTime.now(ZoneId.systemDefault())));
    }

    /**
     * 旧日期对象转新日期对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 格式化日期
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(localDateTime);
    }

    /**
     * 格式化日期
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, DateTimeFormatter df) {
        return df.format(localDateTime);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm:ss
     */
    public static String localDateTimeFormatWithSeconds(LocalDateTime localDateTime) {
        return TIME_OF_SECONDS.format(localDateTime);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd
     */
    public static String localDateTimeFormatWithDay(LocalDateTime localDateTime) {
        return TIME_OF_DAY.format(localDateTime);
    }

    /**
     * 字符串转日期对象
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转日期对象
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转日期对象，格式为 yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return
     */
    public static LocalDateTime parseLocalDateTimeFormatWithSeconds(String localDateTime) {
        return LocalDateTime.from(TIME_OF_SECONDS.parse(localDateTime));
    }
}
