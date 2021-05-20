package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobRegistryRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobRegistryService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(value = "jobRegistryService")
public class JobRegistryServiceImpl implements JobRegistryService {
    private final JobRegistryRepository jobRegistryRepository;

    @Override
    public List<Integer> findDead(Integer timeout, Date nowTime) {
        return jobRegistryRepository.findDead(timeout, nowTime);
    }

    @Override
    public List<JobRegistry> findAll(Integer timeout, Date nowTime) {
        return jobRegistryRepository.findAll(timeout, nowTime);
    }

    @Override
    public void delete(Set<Integer> ids) {
        for(Integer id : ids) {
            jobRegistryRepository.deleteById(Long.valueOf(id));
        }
    }

    @Override
    public void delete(String registryGroup, String registryKey, String registryValue) {
        jobRegistryRepository.delete(registryGroup, registryKey, registryValue);
    }

    @Override
    public void save(JobRegistry jobRegistry) {
        jobRegistryRepository.save(jobRegistry);
    }

    @Override
    public void update(JobRegistry jobRegistry) {
        jobRegistryRepository.save(jobRegistry);
    }

    @Override
    public Integer registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        return jobRegistryRepository.registryUpdate(registryGroup, registryKey, registryValue, updateTime);
    }

    @Override
    public void registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        jobRegistryRepository.registrySave(registryGroup, registryKey, registryValue, updateTime);
    }
}
