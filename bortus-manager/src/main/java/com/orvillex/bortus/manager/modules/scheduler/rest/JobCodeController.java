package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.glue.GlueTypeEnum;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogGlueService;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/code")
public class JobCodeController {
    private JobInfoService jobInfoService;
    private JobLogGlueService jobLogGlueService;

    @Log("主页")
    @RequestMapping("/")
    public ResponseEntity<Object> index(Long jobId) {
        JobInfo jobInfo = jobInfoService.findById(jobId);
        List<JobLogGlue> jobLogGlues = jobLogGlueService.findByJobId(jobId);

        if (jobInfo == null) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        }

        Map<String, Object> ret = new HashMap<>();

        ret.put("GlueTypeEnum", GlueTypeEnum.values());
        ret.put("jobInfo", jobInfo);
        ret.put("jobLogGlues", jobLogGlues);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @Log("保存")
    @PutMapping("/save")
    public ResponseEntity<ReturnT<String>> save(Long id, String glueSource, String glueRemark) {
        ReturnT<String> result = null;
        if (glueRemark == null) {
            result = new ReturnT<String>(500,
                    (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")));
        }
        if (glueRemark.length() < 4 || glueRemark.length() > 100) {
            result = new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
        }
        JobInfo exists_jobInfo = jobInfoService.findById(id);
        if (exists_jobInfo == null) {
            result = new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

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

        return new ResponseEntity<ReturnT<String>>(ReturnT.SUCCESS, HttpStatus.OK);
    }
}
