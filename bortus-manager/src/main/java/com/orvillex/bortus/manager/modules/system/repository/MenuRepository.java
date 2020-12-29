package com.orvillex.bortus.manager.modules.system.repository;

import com.orvillex.bortus.manager.modules.system.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    /**
     * 根据惨淡标题查询
     */
    Menu findByTitle(String title);

    /**
     * 根据组件名称查询
     */
    Menu findByComponentName(String name);

    /**
     * 查询顶级菜单
     */
    List<Menu> findByPid(Long pid);

    /**
     * 查询顶级菜单
     */
    List<Menu> findByPidIsNull();

    @Query(value = "SELECT m.* FROM sys_menu m, sys_roles_menus r WHERE m.menu_id = r.menu_id AND r.role_id IN ?1 AND type != ?2 ORDER BY m.menu_sort ASC", nativeQuery = true)
    LinkedHashSet<Menu> findByRoleIdsAndTypeNot(Set<Long> roleIds, int type);

    /**
     * 获取节点数量
     */
    int countByPid(Long id);

    /**
     * 更新节点数目
     */
    @Modifying
    @Query(value = "UPDATE sys_menu SET sub_count = ?1 WHERE menu_id = ?2", nativeQuery = true)
    void updateSubCntById(int count, Long menuId);
}
