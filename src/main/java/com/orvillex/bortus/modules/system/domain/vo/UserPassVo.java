package com.orvillex.bortus.modules.system.domain.vo;

import lombok.Data;

/**
 * 修改密码
 * @author y-z-f
 * @version 0.1
 */
@Data
public class UserPassVo {
    private String oldPass;

    private String newPass;
}
