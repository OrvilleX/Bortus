package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.job.glue.GlueType;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogGlueService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogGlueDto;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 提供以源码方式运行的任务详情与编辑
 * 
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/code")
public class JobCodeController {
    private final JobInfoService jobInfoService;
    private final JobLogGlueService jobLogGlueService;

    @Log("获取任务")
    @GetMapping("/")
    public ResponseEntity<JobLogGlueDto> detail(Long jobId) {
        JobInfo jobInfo = jobInfoService.findById(jobId);
        List<JobLogGlue> jobLogGlues = jobLogGlueService.findByJobId(jobId);

        if (GlueType.BEAN == GlueType.match(jobInfo.getGlueType())) {
            throw new BadRequestException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        }

        JobLogGlueDto retDto = new JobLogGlueDto();
        
        retDto.setJobInfo(jobInfo);
        retDto.setJobLogGlues(jobLogGlues);

        return new ResponseEntity<>(retDto, HttpStatus.OK);
    }

    @Log("编辑任务")
    @PutMapping("/")
    public ResponseEntity<Object> update(Long id, String glueSource, String glueRemark) {
        if (glueRemark == null) {
            throw new BadRequestException(
                    I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark"));
        }
        if (glueRemark.length() < 4 || glueRemark.length() > 100) {
            throw new BadRequestException(I18nUtil.getString("jobinfo_glue_remark_limit"));
        }

        JobInfo exists_jobInfo = jobInfoService.findById(id);
        exists_jobInfo.setGlueSource(glueSource);
        exists_jobInfo.setGlueRemark(glueRemark);
        exists_jobInfo.setGlueUpdatetime(new Date());
        jobInfoService.update(exists_jobInfo);

        JobLogGlue jobLogGlue = new JobLogGlue();
        jobLogGlue.setJobId(exists_jobInfo.getId());
        jobLogGlue.setGlueType(exists_jobInfo.getGlueType());
        jobLogGlue.setGlueSource(glueSource);
        jobLogGlue.setGlueRemark(glueRemark);
        jobLogGlueService.create(jobLogGlue);

        jobLogGlueService.removeOld(exists_jobInfo.getId(), 30);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
