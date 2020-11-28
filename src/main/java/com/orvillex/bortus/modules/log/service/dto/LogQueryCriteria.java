package com.orvillex.bortus.modules.log.service.dto;

import com.orvillex.bortus.annotation.Query;
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

    @Query(blurry = "username, description, address, requesstIp, method, params")
    private String blurry;

    @Query
    private String logType;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
