package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobLogGlue;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobLogGlueRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogGlueService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(value = "jobLogGlueService")
public class JobLogGlueServiceImpl implements JobLogGlueService {
    private final JobLogGlueRepository jobLogGlueRepository;

    @Override
    public void create(JobLogGlue jobLogGlue) {
        jobLogGlueRepository.save(jobLogGlue);
    }

    @Override
    public List<JobLogGlue> findByJobId(Long jobId) {
        return jobLogGlueRepository.findByJobId(jobId);
    }

    @Override
    public void removeOld(Long jobId, Integer limit) {
        jobLogGlueRepository.removeOld(jobId, limit);
    }

    @Override
    public void deleteByJobId(Long jobId) {
        jobLogGlueRepository.deleteByJobId(jobId);
    }
}
