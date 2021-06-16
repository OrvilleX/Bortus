package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.orvillex.bortus.job.util.DateUtil;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.core.cron.CronExpression;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * 任务信息API
 * 
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "任务信息")
@RequiredArgsConstructor
@RequestMapping("/scheduler/info")
public class JobInfoController {
    private final JobInfoService jobInfoService;
    private final JobService jobService;
    private final JobTriggerPool jobTriggerPool;

    @Log("任务列表")
    @GetMapping()
    @ApiOperation(value = "任务列表")
    public ResponseEntity<BasePage<JobInfo>> pageList(JobInfoCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(jobInfoService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("添加任务")
    @PostMapping()
    @ApiOperation(value = "添加任务")
    public ResponseEntity<Object> create(@Validated @RequestBody JobInfo jobInfo) {
        jobService.add(jobInfo);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("更新任务")
    @PutMapping()
    @ApiOperation(value = "更新任务")
    public ResponseEntity<Object> update(@Validated @RequestBody JobInfo jobInfo) {
        jobService.update(jobInfo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除任务")
    @DeleteMapping()
    @ApiOperation(value = "删除任务")
    public ResponseEntity<Object> remove(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            jobService.remove(id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("根据执行器获取任务")
    @GetMapping(value = "/group/{id}")
    @ApiOperation(value = "根据执行器获取任务")
    public ResponseEntity<List<JobInfo>> getJobsByGroup(@PathVariable Long jobGroup) {
        List<JobInfo> list = jobInfoService.findByJobGroup(jobGroup);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Log("停止任务")
    @PutMapping(value = "/stop/{id}")
    @ApiOperation(value = "停止任务")
    public ResponseEntity<Object> pause(@PathVariable Long id) {
        jobService.stop(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("启动任务")
    @PutMapping(value = "/start/{id}")
    @ApiOperation(value = "启动任务")
    public ResponseEntity<Object> start(@PathVariable Long id) {
        jobService.start(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("触发任务")
    @PutMapping(value = "/trigger")
    @ApiOperation(value = "触发任务")
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
