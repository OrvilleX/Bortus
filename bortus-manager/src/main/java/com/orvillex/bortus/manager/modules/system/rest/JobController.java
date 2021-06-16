package com.orvillex.bortus.manager.modules.system.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.system.domain.Job;
import com.orvillex.bortus.manager.modules.system.service.JobService;
import com.orvillex.bortus.manager.modules.system.service.dto.JobDto;
import com.orvillex.bortus.manager.modules.system.service.dto.JobQueryCriteria;
import com.orvillex.bortus.manager.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 岗位API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "岗位管理")
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class JobController {
    private final JobService jobService;

    @Log("导出岗位数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('job:list')")
    @ApiOperation(value = "导出岗位数据")
    public void download(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        jobService.download(jobService.queryAll(criteria), response);
    }

    @Log("查询岗位")
    @GetMapping
    @PreAuthorize("@x.check('job:list','user:list')")
    @ApiOperation(value = "查询岗位")
    public ResponseEntity<BasePage<JobDto>> query(JobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(jobService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("新增岗位")
    @PostMapping
    @PreAuthorize("@x.check('job:add')")
    @ApiOperation(value = "新增岗位")
    public ResponseEntity<Object> create(@Validated @RequestBody Job resources){
        if (resources.getId() != null) {
            throw new BadRequestException("不能携带ID");
        }
        jobService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改岗位")
    @PutMapping
    @PreAuthorize("@x.check('job:edit')")
    @ApiOperation(value = "修改岗位")
    public ResponseEntity<Object> update(@Validated(Job.Update.class) @RequestBody Job resources){
        jobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除岗位")
    @DeleteMapping
    @PreAuthorize("@x.check('job:del')")
    @ApiOperation(value = "删除岗位")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        // 验证是否被用户关联
        jobService.verification(ids);
        jobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
