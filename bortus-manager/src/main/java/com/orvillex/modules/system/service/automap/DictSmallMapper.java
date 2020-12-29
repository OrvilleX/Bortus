package com.orvillex.modules.system.service.automap;

import com.orvillex.entity.BaseMapper;
import com.orvillex.modules.system.domain.Dict;
import com.orvillex.modules.system.service.dto.DictSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DictSmallDto与Dict转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictSmallMapper extends BaseMapper<DictSmallDto, Dict> {

}
