package com.orvillex.bortus.manager.modules.scheduler.rest;

import com.orvillex.bortus.job.enums.RegistryConfig;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobRegistry;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobRegistryService;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;

import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.utils.I18nUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * 执行器组管理API
 * 
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/group")
public class JobGroupController {
	private final JobInfoService jobInfoService;
	private final JobGroupService jobGroupService;
	private final JobRegistryService jobRegistryService;

	@Log("执行器列表")
	@GetMapping
	public ResponseEntity<BasePage<JobGroup>> pageList(JobGroupCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(jobGroupService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("创建执行器")
	@PostMapping
	public ResponseEntity<Object> create(@Validated @RequestBody JobGroup jobGroup) {
		if (jobGroup.getAppName().length() < 4 || jobGroup.getAppName().length() > 64) {
			throw new BadRequestException(I18nUtil.getString("jobgroup_field_appname_length"));
		}

		jobGroupService.create(jobGroup);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Log("更新执行器")
	@PutMapping
	public ResponseEntity<Object> update(@Validated @RequestBody JobGroup jobGroup) {
		if (jobGroup.getAppName().length() < 4 || jobGroup.getAppName().length() > 64) {
			throw new BadRequestException(I18nUtil.getString("jobgroup_field_appname_length"));
		}
		if (jobGroup.getAddressType() == 0) {
			List<String> registryList = findRegistryByAppName(jobGroup.getAppName());
			String addressListStr = null;
			if (registryList != null && !registryList.isEmpty()) {
				Collections.sort(registryList);
				addressListStr = "";
				for (String item : registryList) {
					addressListStr += item + ",";
				}
				addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
			}
			jobGroup.setAddressList(addressListStr);
		} else {
			if (jobGroup.getAddressList() == null || jobGroup.getAddressList().trim().length() == 0) {
				throw new BadRequestException(I18nUtil.getString("jobgroup_field_addressType_limit"));
			}
			String[] addresss = jobGroup.getAddressList().split(",");
			for (String item : addresss) {
				if (item == null || item.trim().length() == 0) {
					throw new BadRequestException(I18nUtil.getString("jobgroup_field_registryList_unvalid"));
				}
			}
		}

		jobGroupService.update(jobGroup);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private List<String> findRegistryByAppName(String appnameParam) {
		HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
		List<JobRegistry> list = jobRegistryService.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
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
		return appAddressMap.get(appnameParam);
	}

	@Log("删除执行器")
	@DeleteMapping
	public ResponseEntity<Object> remove(@RequestBody Set<Long> ids) {
		for (Long id : ids) {
			JobInfoCriteria criteria = new JobInfoCriteria();
			criteria.setJobGroup(id);
			criteria.setTriggerStatus(-1);
			BasePage<JobInfo> jobInfoList = jobInfoService.queryAll(criteria, PageRequest.of(0, 10));
			Long count = jobInfoList.getTotalElements();

			if (count > 0) {
				throw new BadRequestException(I18nUtil.getString("jobgroup_del_limit_0"));
			}

			List<JobGroup> allList = jobGroupService.queryAll(new JobGroupCriteria());
			if (allList.size() == 1) {
				throw new BadRequestException(I18nUtil.getString("jobgroup_del_limit_1"));
			}
		}
		jobGroupService.delete(ids);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Log("获取执行器明细")
	@GetMapping("/{id}")
	public ResponseEntity<JobGroup> loadById(@PathVariable Long id) {
		JobGroup jobGroup = jobGroupService.findById(id);
		return new ResponseEntity<>(jobGroup, HttpStatus.OK);
	}
}
