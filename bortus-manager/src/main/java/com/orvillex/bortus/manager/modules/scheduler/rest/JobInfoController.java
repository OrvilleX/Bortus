package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orvillex.bortus.job.enums.ExecutorBlockStrategyType;
import com.orvillex.bortus.job.glue.GlueType;
import com.orvillex.bortus.job.util.DateUtil;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.core.cron.CronExpression;
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
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoDto;
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

/**
 * 任务信息API
 * 
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/info")
public class JobInfoController {
    private final JobGroupService jobGroupService;
    private final JobInfoService jobInfoService;
    private final JobService jobService;
    private final JobTriggerPool jobTriggerPool;

    @Log("获取条件参数")
    @GetMapping("/")
    public ResponseEntity<JobInfoDto> index() {
        JobInfoDto retDto = new JobInfoDto();
        retDto.setRoutes(ExecutorRouteStrategyType.values());
        retDto.setGlues(GlueType.values());
        retDto.setBlocks(ExecutorBlockStrategyType.values());

        List<JobGroup> jobGroupList_all = jobGroupService.queryAll(new JobGroupCriteria());

        retDto.setJobGroupList(jobGroupList_all);

        return new ResponseEntity<JobInfoDto>(retDto, HttpStatus.OK);
    }

    @Log("任务列表")
    @GetMapping("/list")
    public ResponseEntity<BasePage<JobInfo>> pageList(JobInfoCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(jobInfoService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("添加任务")
    @PostMapping("/")
    public ResponseEntity<Object> add(JobInfo jobInfo) {
        jobService.add(jobInfo);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("更新任务")
    @PutMapping("/")
    public ResponseEntity<Object> update(JobInfo jobInfo) {
        jobService.update(jobInfo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除任务")
    @DeleteMapping("/")
    public ResponseEntity<Object> remove(Long id) {
        jobService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("停止任务")
    @PutMapping("/stop")
    public ResponseEntity<Object> pause(Long id) {
        jobService.stop(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("启动任务")
    @PutMapping("/start")
    public ResponseEntity<Object> start(Long id) {
        jobService.start(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("触发任务")
    @GetMapping("/trigger")
    public ResponseEntity<Object> triggerJob(Long id, String executorParam, String addressList) {
        if (executorParam == null) {
            executorParam = "";
        }

        jobTriggerPool.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/nextTriggerTime")
    public ResponseEntity<List<String>> nextTriggerTime(String cron) {
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
            throw new BadRequestException(I18nUtil.getString("jobinfo_field_cron_unvalid"));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
