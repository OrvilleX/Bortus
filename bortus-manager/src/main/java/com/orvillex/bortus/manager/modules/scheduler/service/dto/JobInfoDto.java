package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import java.io.Serializable;
import java.util.List;

import com.orvillex.bortus.job.enums.ExecutorBlockStrategyType;
import com.orvillex.bortus.job.glue.GlueType;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorRouteStrategyType;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInfoDto implements Serializable {
    private static final long serialVersionUID = 4601288599435989129L;
    
    private ExecutorRouteStrategyType[] routes;
    private ExecutorBlockStrategyType[] blocks;
    private GlueType[] glues;
    private List<JobGroup> jobGroupList;
}
