package com.orvillex.bortus.manager.modules.system.service.automap;

import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.system.service.dto.RoleSmallDto;
import com.orvillex.bortus.manager.modules.system.domain.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * RoleSmallDto与Role转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleSmallMapper extends BaseMapper<RoleSmallDto, Role> {

}