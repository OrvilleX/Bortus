package com.orvillex.modules.system.service.dto;

import com.orvillex.annotation.Query;
import lombok.Data;

import java.util.List;

/**
 * 角色查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class RoleQueryCriteria {

    @Query(blurry = "name,description")
    private String blurry;

    @Query(type = Query.Type.BETWEEN)
    private List<Integer> createTime;
}
