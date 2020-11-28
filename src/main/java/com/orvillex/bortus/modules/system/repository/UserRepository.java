package com.orvillex.bortus.modules.system.repository;

import com.orvillex.bortus.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户仓储接口
 * @author y-z-f
 * @version 0.1
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查询
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询
     */
    User findByEmail(String email);

    /**
     * 修改密码
     */
    @Modifying
    @Query(value = "UPDATE sys_user SET password = ?2, pwd_reset_time = ?3 WHERE username = ?1", nativeQuery = true)
    void updatePass(String username, String pass, Date lastPasswordResetTime);

    /**
     * 根据角色查询用户
     */
    @Query(value = "SELECT u.* FROM sys_user u, sys_users_roles r WHERE u.user_id = r.user_id AND r.role_id = ?1", nativeQuery = true)
    List<User> findByRoleId(Long roleId);

    /**
     * 根据角色中的部门查询
     */
    @Query(value = "SELECT u.* FROM sys_user u, sys_users_role r, sys_roles_depts d WHERE u.user_id = r.user_id AND r.role_id = d.role_id AND r.role_id = ?1 GROUP BY u.user_id", nativeQuery = true)
    List<User> findByDeptRoleId(Long id);

    /**
     * 根据菜单查询
     */
    @Query(value = "SELECT u.* FROM sys_user u, sys_users_roles ur, sys_roles_menus rm WHERE\n" +
            "u.user_id = ur.user_id AND ur.role_id = rm.role_id AND rm.menu_id = ?1 group by u.user_id", nativeQuery = true)
    List<User> findByMenuId(Long id);

    /**
     * 根据Id删除
     */
    void deleteAllByIdIn(Set<Long> ids);

    /**
     * 根据岗位查询
     */
    @Query(value = "SELECT count(1) FROM sys_user u, sys_users_jobs j WHERE u.user_id = j.user_id AND j.job_id IN ?1", nativeQuery = true)
    int countByJobs(Set<Long> ids);

    /**
     * 根据部门查询
     */
    @Query(value = "SELECT count(1) FROM sys_user u WHERE u.dept_id IN ?1", nativeQuery = true)
    int countByDepts(Set<Long> deptIds);

    /**
     * 根据角色查询
     */
    @Query(value = "SELECT count(1) FROM sys_user u, sys_users_roles r WHERE " +
            "u.user_id = r.user_id AND r.role_id in ?1", nativeQuery = true)
    int countByRoles(Set<Long> ids);
}
