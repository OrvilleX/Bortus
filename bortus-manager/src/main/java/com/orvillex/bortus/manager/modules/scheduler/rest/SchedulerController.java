package com.orvillex.bortus.manager.modules.scheduler.rest;

import java.util.Date;
import java.util.Map;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.scheduler.service.JobService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 任务调度概览信息
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
public class SchedulerController {
    private final JobService jobService;

    @Log("大屏数据")
    @GetMapping(value = "/")
    public ResponseEntity<Object> index() {
        Map<String, Long> dashboardMap = jobService.dashboardInfo();
        return new ResponseEntity<>(dashboardMap, HttpStatus.OK);
    }

    @Log("图表数据")
    @GetMapping(value = "/chartInfo")
    public ResponseEntity<Object> chartInfo(Date startDate, Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = jobService.chartInfo(startDate, endDate);
        return new ResponseEntity<>(chartInfo, HttpStatus.OK);
    }
}
