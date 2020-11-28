package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.annotation.DataPermission;
import com.orvillex.bortus.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 部门查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
@DataPermission(fieldName = "id")
public class DeptQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private Boolean enabled;

    @Query
    private Long pid;

    @Query(type = Query.Type.IS_NULL, propName = "pid")
    private Boolean pidIsNull;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
