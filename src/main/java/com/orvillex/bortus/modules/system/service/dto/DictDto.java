package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.entity.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 字典DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class DictDto extends BaseDTO implements Serializable {
    private Long id;
    private List<DictDetailDto> dictDetails;
    private String name;
    private String description;
}
