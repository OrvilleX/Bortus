package com.orvillex.bortus.manager.modules.system.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.orvillex.bortus.manager.entity.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

/**
 * 用户DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class UserDto extends BaseDTO {
    private Long id;
    private Set<RoleSmallDto> roles;
    private Set<JobSmallDto> jobs;
    private DeptSmallDto dept;
    private Long deptId;
    private String username;
    private String nickName;
    private String email;
    private String phone;
    private String gender;
    private String avatarName;
    private String avatarPath;

    @JSONField(serialize = false)
    private String password;
    private Boolean enabled;

    @JSONField(serialize = false)
    private Boolean isAdmin = false;
    private Date pwdResetTime;
}
