package com.orvillex.bortus.manager.modules.log.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.log.service.LogService;
import com.orvillex.bortus.manager.modules.log.service.dto.LogQueryCriteria;
import com.orvillex.bortus.manager.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 审计日志API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "审计日志")
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {
    private final LogService logService;

    @Log("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check()")
    @ApiOperation(value = "导出数据")
    public void download(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        criteria.setLogType("INFO");
        logService.download(logService.queryAll(criteria), response);
    }

    @Log("导出错误数据")
    @GetMapping(value = "/error/download")
    @PreAuthorize("@x.check()")
    @ApiOperation(value = "导出错误数据")
    public void downloadErrorLog(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        criteria.setLogType("ERROR");
        logService.download(logService.queryAll(criteria), response);
    }

    @GetMapping
    @PreAuthorize("@x.check()")
    public ResponseEntity<Object> query(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/user")
    public ResponseEntity<Object> queryUserLog(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getCurrentUsername());
        return new ResponseEntity<>(logService.queryAllByUser(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/error")
    @PreAuthorize("@x.check()")
    public ResponseEntity<Object> queryErrorLog(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("ERROR");
        return new ResponseEntity<>(logService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/error/{id}")
    @PreAuthorize("@x.check()")
    public ResponseEntity<Object> queryErrorLogs(@PathVariable Long id) {
        return new ResponseEntity<>(logService.findByErrDetail(id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/del/error")
    @Log("删除所有ERROR日志")
    @PreAuthorize("@x.check()")
    @ApiOperation(value = "删除所有ERROR日志")
    public  ResponseEntity<Object> delAllErrorLog() {
        logService.delAllByError();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/del/info")
    @Log("删除所有INFO日志")
    @PreAuthorize("@x.check()")
    @ApiOperation(value = "删除所有INFO日志")
    public ResponseEntity<Object> delAllInfoLog() {
        logService.delAllByInfo();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
