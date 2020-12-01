package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.annotation.DataPermission;
import com.orvillex.bortus.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 部门精简DTO
 * @author y-z-f
 * @version 0.1
 */
@Data
public class DeptSmallDto {
    private Long id;
    private String name;
}
