package com.orvillex.bortus.modules.system.service.dto;

import com.orvillex.bortus.entity.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 字典明细DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class DictDetailDto extends BaseDTO implements Serializable {
    private Long id;
    private DictSmallDto dict;
    private String label;
    private String value;
    private Integer dictSort;
}
