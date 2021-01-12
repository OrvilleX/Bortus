package com.orvillex.bortus.manager.modules.log.service.dto;

import com.orvillex.bortus.manager.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 日志查询
 * @author y-z-f
 * @version 0.1
 */
@Data
public class LogQueryCriteria {
    
    @Query(blurry = "username, description, address, requestIp, method, params")
    private String blurry;

    @Query
    private String logType;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
