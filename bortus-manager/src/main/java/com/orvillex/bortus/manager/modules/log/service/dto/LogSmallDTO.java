package com.orvillex.bortus.manager.modules.log.service.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 精简日志DTO
 * @author y-z-f
 * @version 0.1
 */
@Data
public class LogSmallDTO {
    private String description;
    private String requestIp;
    private Long time;
    private String address;
    private String browser;
    private Timestamp createTime;
}
