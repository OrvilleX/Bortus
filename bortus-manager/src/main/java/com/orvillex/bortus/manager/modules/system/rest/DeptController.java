package com.orvillex.bortus.manager.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.system.domain.Dept;
import com.orvillex.bortus.manager.modules.system.service.DeptService;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptQueryCriteria;
import com.orvillex.bortus.manager.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 部门API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "部门管理")
@RequiredArgsConstructor
@RequestMapping("/api/dept")
public class DeptController {
    private final DeptService deptService;

    @Log("导出部门数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('dept:list')")
    @ApiOperation(value = "导出部门数据")
    public void download(HttpServletResponse response, DeptQueryCriteria criteria) throws Exception {
        deptService.download(deptService.queryAll(criteria, false), response);
    }

    @Log("查询部门")
    @GetMapping
    @PreAuthorize("@x.check('user:list','dept:list')")
    @ApiOperation(value = "查询部门")
    public ResponseEntity<BasePage<DeptDto>> query(DeptQueryCriteria criteria) throws Exception {
        List<DeptDto> deptDtos = deptService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(deptDtos, (long)deptDtos.size()), HttpStatus.OK);
    }

    @Log("查询部门")
    @PostMapping("/superior")
    @PreAuthorize("@x.check('user:list','dept:list')")
    @ApiOperation(value = "查询部门")
    public ResponseEntity<Object> getSuperior(@RequestBody List<Long> ids) {
        Set<DeptDto> deptDtos  = new LinkedHashSet<>();
        for (Long id : ids) {
            DeptDto deptDto = deptService.findById(id);
            List<DeptDto> depts = deptService.getSuperior(deptDto, new ArrayList<>());
            deptDtos.addAll(depts);
        }
        return new ResponseEntity<>(deptService.buildTree(new ArrayList<>(deptDtos)),HttpStatus.OK);
    }

    @Log("新增部门")
    @PostMapping
    @PreAuthorize("@x.check('dept:add')")
    @ApiOperation(value = "新增部门")
    public ResponseEntity<Object> create(@Validated @RequestBody Dept resources){
        if (resources.getId() != null) {
            throw new BadRequestException("不能携带ID");
        }
        deptService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改部门")
    @PutMapping
    @PreAuthorize("@x.check('dept:edit')")
    @ApiOperation(value = "修改部门")
    public ResponseEntity<Object> update(@Validated(Dept.Update.class) @RequestBody Dept resources){
        deptService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除部门")
    @DeleteMapping
    @PreAuthorize("@x.check('dept:del')")
    @ApiOperation(value = "删除部门")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        Set<DeptDto> deptDtos = new HashSet<>();
        for (Long id : ids) {
            List<Dept> deptList = deptService.findByPid(id);
            deptDtos.add(deptService.findById(id));
            if(CollectionUtil.isNotEmpty(deptList)){
                deptDtos = deptService.getDeleteDepts(deptList, deptDtos);
            }
        }
        deptService.verification(deptDtos);
        deptService.delete(deptDtos);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
