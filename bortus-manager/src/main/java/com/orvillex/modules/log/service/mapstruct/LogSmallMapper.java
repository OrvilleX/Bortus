package com.orvillex.modules.log.service.mapstruct;

import com.orvillex.entity.BaseMapper;
import com.orvillex.modules.log.domain.Log;
import com.orvillex.modules.log.service.dto.LogSmallDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * LogSmallDTO与Log转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogSmallMapper extends BaseMapper<LogSmallDTO, Log> {
}
