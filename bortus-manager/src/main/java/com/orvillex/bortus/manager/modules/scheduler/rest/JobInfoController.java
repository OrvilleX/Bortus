package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.glue.GlueType;
import com.orvillex.bortus.job.util.DateUtil;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.scheduler.core.cron.CronExpression;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorBlockStrategyType;
import com.orvillex.bortus.manager.modules.scheduler.core.route.ExecutorRouteStrategyType;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/info")
public class JobInfoController {
    private JobGroupService jobGroupService;
    private JobInfoService jobInfoService;
    private JobService jobService;
    private JobTriggerPool jobTriggerPool;

    @Log("任务基础信息")
    @GetMapping("/")
    public ResponseEntity<Object> index(Long jobGroup) {
        Map<String, Object> retMap = new HashMap<>();

        retMap.put("ExecutorRouteStrategyEnum", ExecutorRouteStrategyType.values());
        retMap.put("GlueTypeEnum", GlueType.values());
        retMap.put("ExecutorBlockStrategyEnum", ExecutorBlockStrategyType.values());

        // 执行器列表
        List<JobGroup> jobGroupList_all = jobGroupService.queryAll(new JobGroupCriteria());

        retMap.put("JobGroupList", jobGroupList_all);
        retMap.put("jobGroup", jobGroup);

        return new ResponseEntity<Object>(retMap, HttpStatus.OK);
    }

    @Log("任务列表")
    @GetMapping("/pageList")
    public ResponseEntity<Object> pageList(JobInfoCriteria criteria, Pageable pageable) {
        return new ResponseEntity<Object>(jobInfoService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("添加任务")
    @PostMapping("/add")
    public ReturnT<String> add(JobInfo jobInfo) {
        return jobService.add(jobInfo);
    }

    @Log("更新")
    @PutMapping("/update")
    public ReturnT<String> update(JobInfo jobInfo) {
        return jobService.update(jobInfo);
    }

    @DeleteMapping("/remove")
    public ReturnT<String> remove(Long id) {
        return jobService.remove(id);
    }

    @PutMapping("/stop")
    public ReturnT<String> pause(Long id) {
        return jobService.stop(id);
    }

    @PutMapping("/start")
    public ReturnT<String> start(Long id) {
        return jobService.start(id);
    }

    @GetMapping("/trigger")
    public ReturnT<String> triggerJob(Long id, String executorParam, String addressList) {
        if (executorParam == null) {
            executorParam = "";
        }

        jobTriggerPool.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }

    @GetMapping("/nextTriggerTime")
    public ReturnT<List<String>> nextTriggerTime(String cron) {
        List<String> result = new ArrayList<>();
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            return new ReturnT<List<String>>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
        }
        return new ReturnT<List<String>>(result);
    }
}
