package com.orvillex.bortus.manager.modules.system.service.dto;

import com.orvillex.bortus.manager.entity.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典明细DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class DictDetailDto extends BaseDTO {
    private Long id;

    private DictSmallDto dict;

    private String label;

    private String value;

    private Integer dictSort;
}
