package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobLogRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.LogReportDto;
import com.orvillex.bortus.manager.utils.EntityUtils;
import com.orvillex.bortus.manager.utils.PageUtil;
import com.orvillex.bortus.manager.utils.QueryHelp;
import com.orvillex.bortus.manager.utils.ValidationUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(value = "jobLogService")
public class JobLogServiceImpl implements JobLogService {
    private final JobLogRepository jobLogRepository;

    @Override
    public BasePage<JobLog> queryAll(JobLogCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(jobLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate autoPredicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            if (criteria.getLogStatus() != null && criteria.getLogStatus() == 1) {
                Predicate logStatus = criteriaBuilder.equal(root.get("handleCode").as(Integer.class), 200);
                return criteriaBuilder.and(autoPredicate, logStatus);
            } else if (criteria.getLogStatus() != null && criteria.getLogStatus() == 2) {
                List<Long> notStatus = Arrays.asList(0L, 200L);
                Predicate triggerCode = criteriaBuilder.not(root.get("triggerCode").in(notStatus));
                Predicate handleCode = criteriaBuilder.not(root.get("handleCode").in(notStatus));
                Predicate triggerOrHandle = criteriaBuilder.or(triggerCode, handleCode);
                return criteriaBuilder.and(autoPredicate, triggerOrHandle);
            } else if (criteria.getLogStatus() != null && criteria.getLogStatus() == 3) {
                Predicate triggerCode = criteriaBuilder.equal(root.get("triggerCode").as(Integer.class), 200L);
                Predicate handleCode = criteriaBuilder.equal(root.get("handleCode").as(Integer.class), 0L);
                return criteriaBuilder.and(autoPredicate, triggerCode, handleCode);
            }
            return autoPredicate;
        }, pageable));
    }

    @Override
    public void create(JobLog jobLog) {
        jobLogRepository.save(jobLog);
    }

    @Override
    public void update(JobLog jobLog) {
        jobLogRepository.save(jobLog);
    }

    @Override
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            jobLogRepository.deleteById(id);
        }
    }

    @Override
    public JobLog findById(Long id) {
        JobLog jobLog = jobLogRepository.findById(id).orElseGet(JobLog::new);
        ValidationUtil.isNull(jobLog.getId(), "JobLog", "id", id);
        return jobLog;
    }

    @Override
    public void updateTriggerInfo(JobLog xxlJobLog) {
        jobLogRepository.updateTriggerInfo(xxlJobLog.getId(), xxlJobLog.getTriggerTime(), xxlJobLog.getTriggerCode(),
                xxlJobLog.getTriggerMsg(), xxlJobLog.getExecutorAddress(), xxlJobLog.getExecutorHandler(),
                xxlJobLog.getExecutorParam(), xxlJobLog.getExecutorShardingParam(),
                xxlJobLog.getExecutorFailRetryCount());
    }

    @Override
    public void updateHandleInfo(JobLog xxlJobLog) {
        jobLogRepository.updateHandleInfo(xxlJobLog.getId(), xxlJobLog.getHandleTime(), xxlJobLog.getHandleCode(),
                xxlJobLog.getHandleMsg());
    }

    @Override
    public LogReportDto findLogReport(Date from, Date to) {
        Object[] result = jobLogRepository.findLogReport(from, to);
        return EntityUtils.caseEntity(result, LogReportDto.class);
    }

    @Override
    public List<Integer> findLostJobIds(Date losedTime) {
        return jobLogRepository.findLostJobIds(losedTime);
    }

    @Override
    public Integer updateAlarmStatus(Long id, Integer oldAlarmStatus, Integer newAlarmStatus) {
        return jobLogRepository.updateAlarmStatus(id, oldAlarmStatus, newAlarmStatus);
    }

    @Override
    public List<Integer> findFailJobLogIds(Long pagesize) {
        return jobLogRepository.findFailJobLogIds(pagesize);
    }
}
