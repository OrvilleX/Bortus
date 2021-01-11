package com.orvillex.bortus.manager.modules.system.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色精简对象
 * @author y-z-f
 * @version 0.1
 */
@Data
public class RoleSmallDto implements Serializable {
    private static final long serialVersionUID = 6153534840447843908L;
    
    private Long id;
    private String name;
    private Integer level;
    private String dataScope;
}
