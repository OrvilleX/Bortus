package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import com.orvillex.bortus.manager.annotation.Query;

import lombok.Data;

/**
 * 执行器组查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class JobGroupCriteria {
    
    @Query(type = Query.Type.INNER_LIKE)
    private String appName;

    @Query(type = Query.Type.INNER_LIKE)
    private String title;
}
