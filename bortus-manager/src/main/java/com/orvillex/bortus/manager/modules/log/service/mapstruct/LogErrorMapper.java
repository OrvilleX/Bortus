package com.orvillex.bortus.manager.modules.log.service.mapstruct;

import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.log.domain.Log;
import com.orvillex.bortus.manager.modules.log.service.dto.LogErrorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * LogErrorDTO与Log转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogErrorMapper extends BaseMapper<LogErrorDTO, Log> {

}
