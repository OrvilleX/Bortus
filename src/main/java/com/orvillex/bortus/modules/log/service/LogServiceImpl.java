package com.orvillex.bortus.modules.log.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.orvillex.bortus.modules.log.domain.Log;
import com.orvillex.bortus.modules.log.repository.LogRepository;
import com.orvillex.bortus.modules.log.service.dto.LogQueryCriteria;
import com.orvillex.bortus.modules.log.service.mapstruct.LogErrorMapper;
import com.orvillex.bortus.modules.log.service.mapstruct.LogSmallMapper;
import com.orvillex.bortus.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;
    private final LogErrorMapper logErrorMapper;
    private final LogSmallMapper logSmallMapper;

    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)), pageable);
        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
            return PageUtil.toPage(page.map(logErrorMapper::toDto));
        }
        return page;
    }

    @Override
    public List<Log> queryAll(LogQueryCriteria criteria) {
        return logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)));
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)), pageable);
        return PageUtil.toPage(page.map(logSmallMapper::toDto));
    }

    @Override
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log loginfo) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.orvillex.bortus.annotation.Log aopLog = method.getAnnotation(com.orvillex.bortus.annotation.Log.class);

        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";
        StringBuilder params = new StringBuilder("{");
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        for (Object argValue : argValues) {
            params.append(argValue).append(" ");
        }

        if (loginfo != null) {
            loginfo.setDescription(aopLog.value());
        }
        assert loginfo != null;
        loginfo.setRequestIp(ip);

        String loginPath = "login";
        if (loginPath.equals(signature.getName())) {
            try {
                username = new JSONObject(argValues.get(0)).get("username").toString();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        loginfo.setAddress(StringUtils.getCityInfo(loginfo.getRequestIp()));
        loginfo.setMethod(methodName);
        loginfo.setUsername(username);
        loginfo.setParams(params.toString() + " }");
        loginfo.setBrowser(browser);
        logRepository.save(loginfo);
    }

    @Override
    public Object findByErrDetail(Long id) {
        Log log = logRepository.findById(id).orElseGet(Log::new);
        ValidationUtil.isNull(log.getId(), "Log", "Id", id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("exception", new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public void download(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP来源", log.getAddress());
            map.put("描述", log.getDescription());
            map.put("浏览器", log.getBrowser());
            map.put("请求耗时/毫秒", log.getTime());
            map.put("异常详情", new String(ObjectUtil.isNotNull(log.getExceptionDetail()) ? log.getExceptionDetail() : "".getBytes()));
            map.put("创建日期", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void delAllByError() {
        logRepository.deleteByLogType("ERROR");
    }

    @Override
    public void delAllByInfo() {
        logRepository.deleteByLogType("INFO");
    }
}
