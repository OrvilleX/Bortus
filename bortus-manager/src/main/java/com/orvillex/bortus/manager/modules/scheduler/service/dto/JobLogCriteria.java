package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import java.sql.Timestamp;
import java.util.List;

import com.orvillex.bortus.manager.annotation.Query;

import lombok.Data;

/**
 * 任务日志查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class JobLogCriteria {
    
    @Query
    private Long jobGroup;

    @Query
    private Long jobId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> triggerTime;

    private Integer logStatus;
}
