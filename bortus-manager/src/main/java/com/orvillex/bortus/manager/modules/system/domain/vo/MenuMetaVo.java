package com.orvillex.bortus.manager.modules.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单元数据
 * @author y-z-f
 * @version 0.1
 */
@Data
@AllArgsConstructor
public class MenuMetaVo implements Serializable {
    private String title;

    private String icon;

    private Boolean noCache;
}
