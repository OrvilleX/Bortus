package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import com.orvillex.bortus.manager.annotation.Query;

import lombok.Data;

/**
 * 任务信息查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class JobInfoCriteria {

    @Query
    private Long jobGroup;

    @Query
    private Integer triggerStatus;

    @Query(type = Query.Type.INNER_LIKE)
    private String jobDesc;

    @Query(type = Query.Type.INNER_LIKE)
    private String executorHandler;

    @Query(type = Query.Type.INNER_LIKE)
    private String author;
}
