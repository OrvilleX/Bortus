package com.orvillex.bortus.manager.modules.system.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.system.domain.DictDetail;
import com.orvillex.bortus.manager.modules.system.service.DictDetailService;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDetailDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDetailQueryCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典详情API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "字典详情")
@RequiredArgsConstructor
@RequestMapping("/api/dictDetail")
public class DictDetailController {
    private final DictDetailService dictDetailService;

    @Log("查询字典详情")
    @GetMapping
    @ApiOperation(value = "查询字典详情")
    public ResponseEntity<BasePage<DictDetailDto>> query(DictDetailQueryCriteria criteria,
                                        @PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable){
        BasePage<DictDetailDto> result = dictDetailService.queryAll(criteria,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("查询多个字典详情")
    @GetMapping(value = "/map")
    @ApiOperation(value = "查询多个字典详情")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName){
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetailDto>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, dictDetailService.getDictByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }

    @Log("新增字典详情")
    @PostMapping
    @PreAuthorize("@x.check('dict:add')")
    @ApiOperation(value = "新增字典详情")
    public ResponseEntity<Object> create(@Validated @RequestBody DictDetail resources){
        if (resources.getId() != null) {
            throw new BadRequestException("不能携带ID");
        }
        dictDetailService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改字典详情")
    @PutMapping
    @PreAuthorize("@x.check('dict:edit')")
    @ApiOperation(value = "修改字典详情")
    public ResponseEntity<Object> update(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources){
        dictDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@x.check('dict:del')")
    @ApiOperation(value = "删除字典详情")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        dictDetailService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
