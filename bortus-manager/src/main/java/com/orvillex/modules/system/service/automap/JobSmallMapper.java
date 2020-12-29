package com.orvillex.modules.system.service.automap;

import com.orvillex.entity.BaseMapper;
import com.orvillex.modules.system.domain.Job;
import com.orvillex.modules.system.service.dto.JobSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * JobSmallDto与Job转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobSmallMapper extends BaseMapper<JobSmallDto, Job> {

}
