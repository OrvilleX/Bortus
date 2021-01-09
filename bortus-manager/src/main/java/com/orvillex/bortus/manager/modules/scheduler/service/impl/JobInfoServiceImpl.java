package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.List;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobInfoRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.utils.PageUtil;
import com.orvillex.bortus.manager.utils.QueryHelp;
import com.orvillex.bortus.manager.utils.ValidationUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(value = "jobInfoService")
public class JobInfoServiceImpl implements JobInfoService {
    private final JobInfoRepository jobInfoRepository;

    @Override
    public Object queryAll(JobInfoCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(jobInfoRepository.findAll(
            (root, criteriaQuery, criteriaBuilder) -> 
            QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable));
    }

    @Override
    public List<JobInfo> queryAll(JobInfoCriteria criteria) {
        return jobInfoRepository.findAll(
            (root, criteriaQuery, criteriaBuilder) -> 
            QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Override
    public void create(JobInfo jobInfo) {
        jobInfoRepository.save(jobInfo);
    }

    @Override
    public void update(JobInfo jobInfo) {
        jobInfoRepository.save(jobInfo);
    }

    @Override
    public void delete(Long id) {
        jobInfoRepository.deleteById(id);
    }

    @Override
    public JobInfo findById(Long id) {
        JobInfo jobInfo = jobInfoRepository.findById(id).orElseGet(JobInfo::new);
        ValidationUtil.isNull(jobInfo.getId(), "JobInfo", "id", id);
        return jobInfo;
    }

    @Override
    public List<JobInfo> findByJobGroup(Long jobGroup) {
        return jobInfoRepository.findByJobGroup(jobGroup);
    }

    @Override
    public List<JobInfo> scheduleJobQuery(Long maxNextTime, Integer pageSize) {
        return jobInfoRepository.scheduleJobQuery(maxNextTime, pageSize);
    }

    @Override
    public void scheduleUpdate(Long id, Long triggerLastTime, Long triggerNextTime, Integer triggerStatus) {
        jobInfoRepository.scheduleUpdate(id, triggerLastTime, triggerNextTime, triggerStatus);
    }

    @Override
    public Long findAllCount() {
        return jobInfoRepository.count();
    }
}
