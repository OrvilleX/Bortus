package com.orvillex.bortus.manager.modules.system.rest;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BaseEntity;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.system.domain.Dict;
import com.orvillex.bortus.manager.modules.system.service.DictService;
import com.orvillex.bortus.manager.modules.system.service.dto.DictDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DictQueryCriteria;
import com.orvillex.bortus.manager.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 字典接口
 * @author y-z-f
 * @version 0.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dict")
public class DictController {
    private final DictService dictService;

    @Log("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('dict:list')")
    public void download(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
        dictService.download(dictService.queryAll(criteria), response);
    }

    @Log("查询字典")
    @GetMapping(value = "/all")
    @PreAuthorize("@x.check('dict:list')")
    public ResponseEntity<List<DictDto>> queryAll(){
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
    }

    @Log("查询字典")
    @GetMapping
    @PreAuthorize("@x.check('dict:list')")
    public ResponseEntity<BasePage<DictDto>> query(DictQueryCriteria resources, Pageable pageable){
        return new ResponseEntity<>(dictService.queryAll(resources,pageable),HttpStatus.OK);
    }

    @Log("新增字典")
    @PostMapping
    @PreAuthorize("@x.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dict resources){
        if (resources.getId() != null) {
            throw new BadRequestException("不能携带ID");
        }
        dictService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改字典")
    @PutMapping
    @PreAuthorize("@x.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(BaseEntity.Update.class) @RequestBody Dict resources){
        dictService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典")
    @DeleteMapping
    @PreAuthorize("@x.check('dict:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        dictService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
