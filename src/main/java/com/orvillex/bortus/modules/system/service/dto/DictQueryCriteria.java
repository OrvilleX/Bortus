package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.annotation.Query;
import lombok.Data;

/**
 * 字段查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class DictQueryCriteria {
    @Query(blurry = "name, description")
    private String blurry;
}
