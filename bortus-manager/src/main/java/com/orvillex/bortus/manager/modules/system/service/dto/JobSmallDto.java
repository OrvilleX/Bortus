package com.orvillex.bortus.manager.modules.system.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 工作精简DTO
 * @author y-z-f
 * @version 0.1
 */
@Data
@NoArgsConstructor
public class JobSmallDto implements Serializable {
    private Long id;
    private String name;
}
