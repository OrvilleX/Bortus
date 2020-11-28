package com.orvillex.bortus.modules.system.service.automap;

import com.orvillex.bortus.entity.BaseMapper;
import com.orvillex.bortus.modules.system.domain.Role;
import com.orvillex.bortus.modules.system.service.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * RoleDto与Role转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring", uses = {MenuMapper.class, DeptMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends BaseMapper<RoleDto, Role> {

}
