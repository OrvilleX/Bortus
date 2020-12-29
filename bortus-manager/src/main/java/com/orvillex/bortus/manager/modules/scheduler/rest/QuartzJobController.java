package com.orvillex.bortus.manager.modules.scheduler.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.modules.scheduler.domain.QuartzJob;
import com.orvillex.bortus.manager.modules.scheduler.service.QuartzJobService;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobQueryCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 调度任务管理API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class QuartzJobController {
    private static final String ENTITY_NAME = "quartzJob";
    private final QuartzJobService quartzJobService;

    @Log("查询定时任务")
    @GetMapping
    @PreAuthorize("@x.check('timing:list')")
    public ResponseEntity<Object> query(JobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(quartzJobService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @Log("导出任务数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('timing:list')")
    public void download(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        quartzJobService.download(quartzJobService.queryAll(criteria), response);
    }

    @Log("导出日志数据")
    @GetMapping(value = "/logs/download")
    @PreAuthorize("@x.check('timing:list')")
    public void downloadLog(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        quartzJobService.downloadLog(quartzJobService.queryAllLog(criteria), response);
    }

    @GetMapping(value = "/logs")
    @PreAuthorize("@x.check('timing:list')")
    public ResponseEntity<Object> queryJobLog(JobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(quartzJobService.queryAllLog(criteria,pageable), HttpStatus.OK);
    }

    @Log("新增定时任务")
    @PostMapping
    @PreAuthorize("@x.check('timing:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody QuartzJob resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        quartzJobService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改定时任务")
    @PutMapping
    @PreAuthorize("@x.check('timing:edit')")
    public ResponseEntity<Object> update(@Validated(QuartzJob.Update.class) @RequestBody QuartzJob resources){
        quartzJobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("更改定时任务状态")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@x.check('timing:edit')")
    public ResponseEntity<Object> update(@PathVariable Long id){
        quartzJobService.updateIsPause(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("执行定时任务")
    @PutMapping(value = "/exec/{id}")
    @PreAuthorize("@x.check('timing:edit')")
    public ResponseEntity<Object> execution(@PathVariable Long id){
        quartzJobService.execution(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除定时任务")
    @DeleteMapping
    @PreAuthorize("@x.check('timing:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        quartzJobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
