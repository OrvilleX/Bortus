package com.orvillex.bortus.manager.modules.system.service.automap;

import com.orvillex.bortus.manager.modules.system.service.dto.DeptSmallDto;
import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.system.domain.Dept;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Dept与DeptSmallDto转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptSmallMapper extends BaseMapper<DeptSmallDto, Dept> {

}
