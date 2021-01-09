package com.orvillex.bortus.manager.modules.scheduler.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import com.orvillex.bortus.job.biz.AdminBiz;
import com.orvillex.bortus.job.biz.models.HandleCallbackParam;
import com.orvillex.bortus.job.biz.models.RegistryParam;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.handler.IJobHandler;
import com.orvillex.bortus.manager.modules.scheduler.core.trigger.JobTriggerPool;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.enums.TriggerTypeEnum;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobInfoService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobLogService;
import com.orvillex.bortus.manager.modules.scheduler.service.JobRegistryService;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service(value = "adminBiz")
public class AdminBizImpl implements AdminBiz {
    private JobLogService jobLogService;
    private JobInfoService jobInfoService;
    private JobRegistryService jobRegistryService;
    private JobTriggerPool jobTriggerPool;

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            log.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode() == IJobHandler.SUCCESS.getCode() ? "success" : "fail"),
                    handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        JobLog log = jobLogService.findById(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");
        }

        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            JobInfo jobInfo = jobInfoService.findById(log.getJobId());
            if (jobInfo != null && jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" >"
                        + I18nUtil.getString("jobconf_trigger_child_run") + "<<<<<<<<<<< </span><br>";

                String[] childJobIds = jobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    Long childJobId = (childJobIds[i] != null && childJobIds[i].trim().length() > 0
                            && isNumeric(childJobIds[i])) ? Long.valueOf(childJobIds[i]) : -1;
                    if (childJobId > 0) {

                        jobTriggerPool.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"), (i + 1),
                                childJobIds.length, childJobIds[i],
                                (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE
                                        ? I18nUtil.getString("system_success")
                                        : I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"), (i + 1),
                                childJobIds.length, childJobIds[i]);
                    }
                }
            }
        }

        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        if (handleMsg.length() > 15000) {
            handleMsg = new StringBuffer(handleMsg.substring(0, 15000)); // text最大64kb 避免长度过长
        }

        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        jobLogService.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        int ret = jobRegistryService.registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                registryParam.getRegistryValue(), new Date());
        if (ret < 1) {
            jobRegistryService.registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                    registryParam.getRegistryValue(), new Date());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        jobRegistryService.delete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }
}
