package com.orvillex.bortus.manager.modules.system.service.automap;

import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.system.domain.Dept;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Depth与DeptDto转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptMapper extends BaseMapper<DeptDto, Dept> {
}
