package com.orvillex.bortus.modules.scheduler.service.dto;

import com.orvillex.bortus.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 任务查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class JobQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String jobName;

    @Query
    private Boolean isSuccess;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
