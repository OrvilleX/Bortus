package com.orvillex.bortus.manager.modules.system.service.automap;

import com.orvillex.bortus.manager.modules.system.domain.Job;
import com.orvillex.bortus.manager.entity.BaseMapper;
import com.orvillex.bortus.manager.modules.system.service.dto.JobDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * JobDto与Job转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",uses = {DeptMapper.class},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapper extends BaseMapper<JobDto, Job> {
}
