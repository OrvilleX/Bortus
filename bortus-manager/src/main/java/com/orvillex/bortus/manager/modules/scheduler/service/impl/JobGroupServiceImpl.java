package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import com.orvillex.bortus.job.enums.RegistryConfig;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobGroupRepository;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobInfoRepository;
import com.orvillex.bortus.manager.modules.scheduler.repository.JobRegistryRepository;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.utils.PageUtil;
import com.orvillex.bortus.manager.utils.QueryHelp;
import com.orvillex.bortus.manager.utils.ValidationUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * 执行器组服务实现
 * @author y-z-f
 * @version 0.1
 */
@RequiredArgsConstructor
@Service(value = "jobGroupService")
public class JobGroupServiceImpl implements JobGroupService {
    private final JobGroupRepository jobGroupRepository;
    private final JobInfoRepository jobInfoRepository;
    private final JobRegistryRepository jobRegistryRepository;

    @Override
    public BasePage<JobGroup> queryAll(JobGroupCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(jobGroupRepository.findAll(
            (root, criteriaQuery, criteriaBuilder) -> 
            QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable));
    }

    @Override
    public List<JobGroup> queryAll(JobGroupCriteria criteria) {
        return jobGroupRepository.findAll(
            (root, criteriaQuery, criteriaBuilder) -> 
            QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    @Override
    public List<JobGroup> findByAddressType(Integer addressType) {
        return jobGroupRepository.findByAddressType(addressType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(JobGroup resources) {
        if (resources.getAddressType() != 0) {
            validAddressList(resources.getAddressList());
        }
        jobGroupRepository.save(resources);
    }

    @Override
    public void update(JobGroup resources) {
        if (resources.getAddressType() == 0) {
            List<String> registryList = findRegistryByAppName(resources.getAppName());
            String addressListStr = null;
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item : registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
            }
            resources.setAddressList(addressListStr);
        } else {
            validAddressList(resources.getAddressList());
        }
        jobGroupRepository.save(resources);
    }

    @Override
    public void delete(Set<Long> ids) {
        ArrayList<Long> idList = new ArrayList<Long>(ids);
        Long id = idList.get(0);
        JobInfoCriteria jobInfoCriteria = new JobInfoCriteria();
        jobInfoCriteria.setJobGroup(id);
        long count = jobInfoRepository.count((root, criteriaQuery, criteriaBuilder) -> 
        QueryHelp.getPredicate(root, jobInfoCriteria, criteriaBuilder));
        if (count > 0) {
            throw new BadRequestException("拒绝删除，该执行器使用中");
        }

        jobGroupRepository.deleteById(id);
    }

    @Override
    public JobGroup findById(Long id) {
        JobGroup jobGroup = jobGroupRepository.findById(id).orElseGet(JobGroup::new);
        ValidationUtil.isNull(jobGroup.getId(), "JobGroup", "id", id);
        return jobGroup;
    }

    private void validAddressList(String addressList) {
        if (addressList == null ||
        addressList.trim().length() == 0) {
            throw new BadRequestException("手动录入方式机器地址不能为空");
        }
        String[] address = addressList.split(",");
        for (String item : address) {
            if (item == null || item.trim().length() == 0) {
                throw new BadRequestException("机器地址格式错误");
            }
        }
    }

    /**
     * 根据应用名获取已注册的服务器，便于自动注册服务器地址
     */
    private List<String> findRegistryByAppName(String appNameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<JobRegistry> list = jobRegistryRepository.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (list != null) {
            for (JobRegistry item : list) {
                if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appNameParam);
    }
}
