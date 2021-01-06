package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.Date;
import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogReport;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobLogReportRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogReportService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(value = "jobLogReportService")
public class JobLogReportServiceImpl implements JobLogReportService {
    private final JobLogReportRepository jobLogReportRepository;

    @Override
    public void save(JobLogReport jobLogReport) {
        jobLogReportRepository.save(jobLogReport);
    }

    @Override
    public void updateByTriggerDay(JobLogReport jobLogReport) {
        jobLogReportRepository.updateByTriggerDay(jobLogReport);
    }

    @Override
    public List<JobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        return jobLogReportRepository.queryLogReport(triggerDayFrom, triggerDayTo);
    }

    @Override
    public JobLogReport queryLogReportTotal() {
        return jobLogReportRepository.queryLogReportTotal();
    }
}
