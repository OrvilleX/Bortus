package com.orvillex.bortus.manager.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.system.domain.Menu;
import com.orvillex.bortus.manager.utils.SecurityUtils;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.system.service.MenuService;
import com.orvillex.bortus.manager.modules.system.service.automap.MenuMapper;
import com.orvillex.bortus.manager.modules.system.service.dto.MenuDto;
import com.orvillex.bortus.manager.modules.system.service.dto.MenuQueryCriteria;
import com.orvillex.bortus.manager.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 菜单管理API
 * @author y-z-f
 * @version 0.1
 */
@RestController
@Api(tags = "菜单管理")
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {
    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @Log("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@x.check('menu:list')")
    @ApiOperation(value = "导出菜单数据")
    public void download(HttpServletResponse response, MenuQueryCriteria criteria) throws Exception {
        menuService.download(menuService.queryAll(criteria, false), response);
    }

    @GetMapping(value = "/build")
    public ResponseEntity<Object> buildMenus(){
        List<MenuDto> menuDtoList = menuService.findByUser(SecurityUtils.getCurrentUserId());
        List<MenuDto> menuDtos = menuService.buildTree(menuDtoList);
        return new ResponseEntity<>(menuService.buildMenus(menuDtos), HttpStatus.OK);
    }

    @GetMapping(value = "/lazy")
    @PreAuthorize("@x.check('menu:list','roles:list')")
    public ResponseEntity<List<MenuDto>> query(@RequestParam Long pid){
        return new ResponseEntity<>(menuService.getMenus(pid),HttpStatus.OK);
    }

    @Log("查询菜单")
    @GetMapping
    @PreAuthorize("@x.check('menu:list')")
    @ApiOperation(value = "查询菜单")
    public ResponseEntity<BasePage<MenuDto>> query(MenuQueryCriteria criteria) throws Exception {
        List<MenuDto> menuDtoList = menuService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(menuDtoList, (long)menuDtoList.size()),HttpStatus.OK);
    }

    @Log("查询菜单")
    @PostMapping("/superior")
    @PreAuthorize("@x.check('menu:list')")
    @ApiOperation(value = "查询菜单")
    public ResponseEntity<Object> getSuperior(@RequestBody List<Long> ids) {
        Set<MenuDto> menuDtos = new LinkedHashSet<>();
        if(CollectionUtil.isNotEmpty(ids)){
            for (Long id : ids) {
                MenuDto menuDto = menuService.findById(id);
                menuDtos.addAll(menuService.getSuperior(menuDto, new ArrayList<>()));
            }
            return new ResponseEntity<>(menuService.buildTree(new ArrayList<>(menuDtos)),HttpStatus.OK);
        }
        return new ResponseEntity<>(menuService.getMenus(null),HttpStatus.OK);
    }

    @Log("新增菜单")
    @PostMapping
    @PreAuthorize("@x.check('menu:add')")
    @ApiOperation(value = "新增菜单")
    public ResponseEntity<Object> create(@Validated @RequestBody Menu resources){
        if (resources.getId() != null) {
            throw new BadRequestException("不能携带ID");
        }
        menuService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改菜单")
    @PutMapping
    @PreAuthorize("@x.check('menu:edit')")
    @ApiOperation(value = "修改菜单")
    public ResponseEntity<Object> update(@Validated(Menu.Update.class) @RequestBody Menu resources){
        menuService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除菜单")
    @DeleteMapping
    @PreAuthorize("@x.check('menu:del')")
    @ApiOperation(value = "删除菜单")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        Set<Menu> menuSet = new HashSet<>();
        for (Long id : ids) {
            List<MenuDto> menuList = menuService.getMenus(id);
            menuSet.add(menuService.findOne(id));
            menuSet = menuService.getDeleteMenus(menuMapper.toEntity(menuList), menuSet);
        }
        menuService.delete(menuSet);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
