package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.models.KillParam;
import com.orvillex.bortus.job.biz.models.LogParam;
import com.orvillex.bortus.job.biz.models.LogResult;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogCriteria;
import com.orvillex.bortus.manager.utils.I18nUtil;
import com.orvillex.bortus.manager.utils.JobSchedulerUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/log")
public class JobLogController {
    private JobGroupService jobGroupService;
    private JobInfoService jobInfoService;
    private JobLogService jobLogService;

    @GetMapping
    public ResponseEntity<Object> index(Long jobId) {
        List<JobGroup> jobGroupList_all = jobGroupService.queryAll(new JobGroupCriteria());
        Map<String, Object> retMap = new HashMap<>();

        retMap.put("JobGroupList", jobGroupList_all);

        // 任务
        if (jobId > 0) {
            JobInfo jobInfo = jobInfoService.findById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException(
                        I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }
            retMap.put("jobInfo", jobInfo);
        }

        return new ResponseEntity<>(retMap, HttpStatus.OK);
    }

    @GetMapping("/getJobsByGroup")
    public ReturnT<List<JobInfo>> getJobsByGroup(Long jobGroup) {
        List<JobInfo> list = jobInfoService.findByJobGroup(jobGroup);
        return new ReturnT<List<JobInfo>>(list);
    }

    @GetMapping("/pageList")
    public ResponseEntity<Object> pageList(JobLogCriteria criteria, Pageable pageable) {
        return new ResponseEntity<Object>(jobLogService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/logDetailPage")
    public ResponseEntity<Object> logDetailPage(Long id) {

        ReturnT<String> logStatue = ReturnT.SUCCESS;
        JobLog jobLog = jobLogService.findById(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }

        return new ResponseEntity<Object>(jobLog, HttpStatus.OK);
    }

    @GetMapping("/logDetailCat")
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
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

            return logResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @DeleteMapping("/logKill")
    public ReturnT<String> logKill(Long id) {
        JobLog joLog = jobLogService.findById(id);
        JobInfo jobInfo = jobInfoService.findById(joLog.getJobId());
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != joLog.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
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
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }
}
