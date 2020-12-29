package com.orvillex.modules.system.service;

import com.orvillex.modules.system.domain.Menu;
import com.orvillex.modules.system.domain.vo.MenuVo;
import com.orvillex.modules.system.service.dto.MenuDto;
import com.orvillex.modules.system.service.dto.MenuQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 菜单服务
 * @author y-z-f
 * @version 0.1
 */
public interface MenuService {

    /**
     * 查询全部数据
     */
    List<MenuDto> queryAll(MenuQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     */
    MenuDto findById(long id);

    /**
     * 创建
     */
    void create(Menu resources);

    /**
     * 编辑
     */
    void update(Menu resources);

    /**
     * 获取待删除的菜单
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 构建菜单树
     */
    List<MenuDto> buildTree(List<MenuDto> menuDtos);

    /**
     * 构建菜单树
     */
    List<MenuVo> buildMenus(List<MenuDto> menuDtos);

    /**
     * 根据ID查询
     */
    Menu findOne(Long id);

    /**
     * 删除
     */
    void delete(Set<Menu> menuSet);

    /**
     * 导出
     */
    void download(List<MenuDto> menuDtos, HttpServletResponse response) throws IOException;

    /**
     * 懒加载菜单数据
     */
    List<MenuDto> getMenus(Long pid);

    /**
     * 根据ID获取同级与上级数据
     */
    List<MenuDto> getSuperior(MenuDto menuDto, List<Menu> menus);

    /**
     * 根据当前用户获取菜单
     */
    List<MenuDto> findByUser(Long currentUserId);
}
