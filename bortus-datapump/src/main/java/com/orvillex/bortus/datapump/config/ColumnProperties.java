package com.orvillex.bortus.datapump.config;

import lombok.Data;
import java.util.List;

/**
 * 列自动转换格式化配置
 */
@Data
public class ColumnProperties {
    private String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    private String dateFormat = "yyyy-MM-dd";
    private String timeFormat = "HH:mm:ss";
    private List<String> extraFormats;
    private String timeZone = "GMT+8";
    private String encoding = "UTF-8";
}
