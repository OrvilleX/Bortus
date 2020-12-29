package com.orvillex.bortus.manager.modules.log.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 日志DTO
 * @author y-z-f
 * @version 0.1
 */
@Data
public class LogErrorDTO implements Serializable {
    private static final long serialVersionUID = -287560234818625002L;

    private Long id;
    private String username;
    private String description;
    private String method;
    private String params;
    private String browser;
    private String requestIp;
    private String address;
    private Timestamp createTime;
}
