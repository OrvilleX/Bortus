package com.orvillex.bortus.manager.modules.system.service.dto;

import com.orvillex.bortus.manager.annotation.Query;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户查询对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class UserQueryCriteria implements Serializable {
    private static final long serialVersionUID = 4237327331152665267L;

    @Query
    private Long id;

    @Query(propName = "id", type = Query.Type.IN, joinName = "dept")
    private Set<Long> deptIds = new HashSet<>();

    @Query(blurry = "email,username,nickName")
    private String blurry;

    @Query
    private Boolean enabled;

    private Long deptId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
