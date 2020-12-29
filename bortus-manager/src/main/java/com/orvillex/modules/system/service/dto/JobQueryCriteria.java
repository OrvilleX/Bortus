package com.orvillex.modules.system.service.dto;

import com.orvillex.annotation.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * 工作查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
@NoArgsConstructor
public class JobQueryCriteria {
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private Boolean enabled;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
