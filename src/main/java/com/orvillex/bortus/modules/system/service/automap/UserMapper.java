package com.orvillex.bortus.modules.system.service.automap;

import com.orvillex.bortus.entity.BaseMapper;
import com.orvillex.bortus.modules.system.domain.User;
import com.orvillex.bortus.modules.system.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * UserDto与User转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",uses = {RoleMapper.class, DeptMapper.class, JobMapper.class},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<UserDto, User> {
}
