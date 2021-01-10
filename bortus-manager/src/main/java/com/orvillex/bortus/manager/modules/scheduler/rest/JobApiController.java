package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orvillex.bortus.job.biz.AdminBiz;
import com.orvillex.bortus.job.biz.models.HandleCallbackParam;
import com.orvillex.bortus.job.biz.models.RegistryParam;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.util.JobRemotingUtil;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.config.scheduler.SchedulerProperties;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 执行器RPC
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduler/job")
public class JobApiController {
    private AdminBiz adminBiz;
    private SchedulerProperties schedulerProperties;

    @Log("管理RPC")
    @RequestMapping("/{uri}")
    @ResponseBody
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri,
            @RequestBody(required = false) String data) {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (schedulerProperties.getAccessToken() != null && schedulerProperties.getAccessToken().trim().length() > 0
                && !schedulerProperties.getAccessToken()
                        .equals(request.getHeader(JobRemotingUtil.BORTUS_JOB_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        if ("callback".equals(uri)) {
            TypeReference<List<HandleCallbackParam>> typeRef = new TypeReference<List<HandleCallbackParam>>() {
            };
            List<HandleCallbackParam> callbackParamList = JSON.parseObject(data, typeRef);
            return adminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri)) {
            RegistryParam registryParam = JSON.parseObject(data, RegistryParam.class);
            return adminBiz.registry(registryParam);
        } else if ("registryRemove".equals(uri)) {
            RegistryParam registryParam = JSON.parseObject(data, RegistryParam.class);
            return adminBiz.registryRemove(registryParam);
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }
    }
}
