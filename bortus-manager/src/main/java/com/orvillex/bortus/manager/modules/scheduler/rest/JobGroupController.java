package com.orvillex.bortus.manager.modules.scheduler.rest;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.enums.RegistryConfig;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
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
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/group")
public class JobGroupController {
	private JobInfoService jobInfoService;
	private JobGroupService jobGroupService;
	private JobRegistryService jobRegistryService;

	@Log("获取任务组列表")
	@GetMapping("/pageList")
	public ResponseEntity<Object> pageList(JobGroupCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(jobGroupService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("创建任务组")
	@PostMapping("/save")
	public ResponseEntity<ReturnT<String>> save(JobGroup jobGroup){
		ReturnT<String> result = null;
		if (jobGroup.getAppname()==null || jobGroup.getAppname().trim().length()==0) {
			result = new ReturnT<String>(500, (I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (jobGroup.getAppname().length()<4 || jobGroup.getAppname().length()>64) {
			result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length") );
		}
		if (jobGroup.getTitle()==null || jobGroup.getTitle().trim().length()==0) {
			result = new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		if (jobGroup.getAddressType()!=0) {
			if (jobGroup.getAddressList()==null || jobGroup.getAddressList().trim().length()==0) {
				result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			String[] addresss = jobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		if (result != null) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		jobGroupService.create(jobGroup);
		return new ResponseEntity<>(ReturnT.SUCCESS, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<ReturnT<String>> update(JobGroup jobGroup){
		ReturnT<String> result = null;
		if (jobGroup.getAppname()==null || jobGroup.getAppname().trim().length()==0) {
			result = new ReturnT<String>(500, (I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (jobGroup.getAppname().length()<4 || jobGroup.getAppname().length()>64) {
			result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length") );
		}
		if (jobGroup.getTitle()==null || jobGroup.getTitle().trim().length()==0) {
			result = new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		if (jobGroup.getAddressType() == 0) {
			// 0=自动注册
			List<String> registryList = findRegistryByAppName(jobGroup.getAppname());
			String addressListStr = null;
			if (registryList!=null && !registryList.isEmpty()) {
				Collections.sort(registryList);
				addressListStr = "";
				for (String item:registryList) {
					addressListStr += item + ",";
				}
				addressListStr = addressListStr.substring(0, addressListStr.length()-1);
			}
			jobGroup.setAddressList(addressListStr);
		} else {
			if (jobGroup.getAddressList()==null || jobGroup.getAddressList().trim().length()==0) {
				result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			String[] addresss = jobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					result = new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		if (result != null) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		jobGroupService.update(jobGroup);
		return new ResponseEntity<>(ReturnT.SUCCESS, HttpStatus.OK);
	}

	private List<String> findRegistryByAppName(String appnameParam){
		HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
		List<JobRegistry> list = jobRegistryService.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
		if (list != null) {
			for (JobRegistry item: list) {
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

	@Log("删除")
	@DeleteMapping("/remove")
	public ResponseEntity<ReturnT<String>> remove(Long id){

		JobInfoCriteria criteria = new JobInfoCriteria();
		criteria.setJobGroup(id);
		criteria.setTriggerStatus(-1);
		Map<String, Object> jobInfoList = (Map<String, Object>)jobInfoService.queryAll(criteria, PageRequest.of(0, 10));
		Long count = (Long) jobInfoList.get("totalElement");

		if (count > 0) {
			return new ResponseEntity<>(new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_0")), HttpStatus.OK);
		}

		List<JobGroup> allList = jobGroupService.queryAll(new JobGroupCriteria());
		if (allList.size() == 1) {
			return new ResponseEntity<>(new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_1")), HttpStatus.OK);
		}

		jobGroupService.delete(new HashSet<Long>(){{ add(id); }});
		return new ResponseEntity<>(ReturnT.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/loadById")
	public ResponseEntity<ReturnT<JobGroup>> loadById(Long id){
		JobGroup jobGroup = jobGroupService.findById(id);
		return new ResponseEntity<>(new ReturnT<JobGroup>(jobGroup), HttpStatus.OK);
	}
}
