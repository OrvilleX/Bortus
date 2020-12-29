package com.orvillex.modules.system.repository;

import com.orvillex.modules.system.domain.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * 部门仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {

    /**
     * 根据父部门编号查询子部门列表
     */
    List<Dept> findByPid(Long id);

    /**
     * 获取顶级部门
     */
    List<Dept> findByPidIsNull();

    @Query(value = "SELECT d.* FROM sys_dept d, sys_roles_depts r WHERE d.dept_id = r.dept_id AND r.role_id = ?1", nativeQuery = true)
    Set<Dept> findByRoleId(Long roleId);

    /**
     * 获取子部门数量
     */
    int countByPid(Long pid);

    /**
     * 根据ID更新sub_count
     */
    @Modifying
    @Query(value = "UPDATE sys_dept SET sub_count = ?1 WHERE dept_id = ?2", nativeQuery = true)
    void updateSubCntById(Integer count, Long id);
}
