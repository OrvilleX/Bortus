package com.orvillex.bortus.manager.utils;

/**
 * 缓存标识
 * @author y-z-f
 * @version 0.1
 */
public interface CacheKey {
    
    /**
     * 用户变更前缀
     */
    String USER_MODIFY_TIME_KEY = "user:modify:time:key:";

    /**
     * 岗位变更前缀
     */
    String JOB_MODIFY_TIME_KEY = "job:modify:time:key:";

    /***
     * 菜单变更前缀
     */
    String MENU_MODIFY_TIME_KEY = "menu:modify:time:key:";

    /**
     * 角色变更前缀
     */
    String ROLE_MODIFY_TIME_KEY = "role:modify:time:key:";

    /**
     * 部门变更前缀
     */
    String DEPT_MODIFY_TIME_KEY = "dept:modify:time:key:";

    /**
     * 用户信息
     */
    String USER_ID = "user::id:";
    String USER_NAME = "user::username:";

    /**
     * 数据
     */
    String DATE_USER = "data::user:";

    /**
     * 菜单
     */
    String MENU_USER = "menu::user:";

    /**
     * 角色授权
     */
    String ROLE_AUTH = "role::auth:";

    /**
     * 角色信息
     */
    String ROLE_ID = "role::id:";

    /**
     * 通过手机号重置邮箱
     */
    String PHONE_RESET_EMAIL_CODE = "phone_reset_email_code_";

    /**
     * 通过旧邮箱重置邮箱
     */
    String EMAIL_RESET_EMAIL_CODE = "email_reset_email_code_";

    /**
     * 通过手机号重置密码
     */
    String PHONE_RESET_PWD_CODE = "phone_reset_pwd_code_";

    /**
     * 通过邮箱重置密码
     */
    String EMAIL_RESET_PWD_CODE = "email_reset_pwd_code_";
}
