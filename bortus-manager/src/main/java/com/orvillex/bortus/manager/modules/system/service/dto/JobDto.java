package com.orvillex.bortus.manager.modules.system.service.dto;

import com.orvillex.bortus.manager.entity.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 工作DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@NoArgsConstructor
public class JobDto extends BaseDTO implements Serializable {
    private Long id;
    private Integer jobSort;
    private String name;
    private Boolean enabled;

    public JobDto(String name, Boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
}
