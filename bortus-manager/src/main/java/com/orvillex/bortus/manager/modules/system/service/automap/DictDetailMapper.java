package com.orvillex.bortus.manager.modules.system.service.automap;

import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.system.domain.DictDetail;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * DictDetailDto与DictDetail转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring", uses = {DictSmallMapper.class}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface DictDetailMapper extends BaseMapper<DictDetailDto, DictDetail> {

}
