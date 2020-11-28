package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.annotation.Query;
import lombok.Data;

/**
 * 字典明细查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class DictDetailQueryCriteria {

    @Query(type = Query.Type.INNER_LIKE)
    private String label;

    @Query(propName = "name",joinName = "dict")
    private String dictName;
}
