package com.orvillex.bortus.modules.system.service.automap;

import com.orvillex.bortus.entity.BaseMapper;
import com.orvillex.bortus.modules.system.domain.Dict;
import com.orvillex.bortus.modules.system.service.dto.DictDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DictDto与Dict转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictMapper extends BaseMapper<DictDto, Dict> {

}
