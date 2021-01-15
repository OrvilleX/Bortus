package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import java.io.Serializable;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;

import lombok.Getter;
import lombok.Setter;

/**
 * 基于源码调度任务DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class JobLogGlueDto implements Serializable {
    private static final long serialVersionUID = -1463968244169141567L;

    private JobInfo jobInfo;
    private List<JobLogGlue> jobLogGlues;
}
