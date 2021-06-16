package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.Date;
import java.util.Set;

import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.models.KillParam;
import com.orvillex.bortus.job.biz.models.LogParam;
import com.orvillex.bortus.job.biz.models.LogResult;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogCriteria;
import com.orvillex.bortus.manager.utils.I18nUtil;
import com.orvillex.bortus.manager.utils.JobSchedulerUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "任务日志")
@RequiredArgsConstructor
@RequestMapping("/scheduler/log")
public class JobLogController {
    private final JobInfoService jobInfoService;
    private final JobLogService jobLogService;

    @Log("获取日志列表")
    @GetMapping()
    @ApiOperation(value = "获取日志列表")
    public ResponseEntity<BasePage<JobLog>> pageList(JobLogCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(jobLogService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("获取日志详情")
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取日志详情")
    public ResponseEntity<Object> logDetailPage(@PathVariable Long id) {
        JobLog jobLog = jobLogService.findById(id);
        return new ResponseEntity<Object>(jobLog, HttpStatus.OK);
    }

    @Log("获取执行器上任务日志")
    @GetMapping(value = "/executor")
    @ApiOperation(value = "获取执行器上任务日志")
    public ResponseEntity<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId,
            int fromLineNum) {
        try {
            ExecutorBiz executorBiz = JobSchedulerUtils.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

            if (logResult.getContent() != null
                    && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                JobLog jobLog = jobLogService.findById(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return new ResponseEntity<>(logResult.getContent(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @Log("删除日志")
    @DeleteMapping()
    @ApiOperation(value = "删除日志")
    public ResponseEntity<Object> logKill(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            JobLog joLog = jobLogService.findById(id);
            JobInfo jobInfo = jobInfoService.findById(joLog.getJobId());

            if (ReturnT.SUCCESS_CODE != joLog.getTriggerCode()) {
                throw new BadRequestException(I18nUtil.getString("joblog_kill_log_limit"));
            }

            ReturnT<String> runResult = null;
            try {
                ExecutorBiz executorBiz = JobSchedulerUtils.getExecutorBiz(joLog.getExecutorAddress());
                runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                runResult = new ReturnT<String>(500, e.getMessage());
            }

            if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
                joLog.setHandleCode(ReturnT.FAIL_CODE);
                joLog.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":"
                        + (runResult.getMsg() != null ? runResult.getMsg() : ""));
                joLog.setHandleTime(new Date());
                jobLogService.updateHandleInfo(joLog);
            } else {
                throw new BadRequestException(runResult.getMsg());
            }
        }
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
