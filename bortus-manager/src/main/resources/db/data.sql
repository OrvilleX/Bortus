
-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (2, 7, 1, '研发部', 3, b'1', 'admin', 'admin', '2019-03-25 09:15:32', '2020-08-02 14:48:47');
INSERT INTO `sys_dept` VALUES (5, 7, 0, '运维部', 4, b'1', 'admin', 'admin', '2019-03-25 09:20:44', '2020-05-17 14:27:27');
INSERT INTO `sys_dept` VALUES (6, 8, 0, '测试部', 6, b'1', 'admin', 'admin', '2019-03-25 09:52:18', '2020-06-08 11:59:21');
INSERT INTO `sys_dept` VALUES (7, NULL, 2, '华南分部', 0, b'1', 'admin', 'admin', '2019-03-25 11:04:50', '2020-06-08 12:08:56');
INSERT INTO `sys_dept` VALUES (8, NULL, 2, '华北分部', 1, b'1', 'admin', 'admin', '2019-03-25 11:04:53', '2020-05-14 12:54:00');
INSERT INTO `sys_dept` VALUES (15, 8, 0, 'UI部门', 7, b'1', 'admin', 'admin', '2020-05-13 22:56:53', '2020-05-14 12:54:13');
INSERT INTO `sys_dept` VALUES (17, 2, 0, '研发一组', 999, b'1', 'admin', 'admin', '2020-08-02 14:49:07', '2020-08-02 14:49:07');
COMMIT;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict` VALUES (1, 'user_status', '用户状态', NULL, NULL, '2019-10-27 20:31:36', NULL);
INSERT INTO `sys_dict` VALUES (4, 'dept_status', '部门状态', NULL, NULL, '2019-10-27 20:31:36', NULL);
INSERT INTO `sys_dict` VALUES (5, 'job_status', '岗位状态', NULL, NULL, '2019-10-27 20:31:36', NULL);
INSERT INTO `sys_dict` VALUES (13, 'glue_type', '运行模式', NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict` VALUES (14, 'route_strategy_type', '路由策略', NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict` VALUES (15, 'block_strategy_type', '阻塞方案', NULL, NULL, '2021-05-07 14:28:00', NULL);
COMMIT;

-- ----------------------------
-- Records of sys_dict_detail
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_detail` VALUES (28, 13, 'BEAN', 'BEAN', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (29, 13, 'GLUE(Java)', 'GLUE_GROOVY', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (30, 13, 'GLUE(Shell)', 'GLUE_SHELL', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (31, 13, 'GLUE(Python)', 'GLUE_PYTHON', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (32, 13, 'GLUE(PHP)', 'GLUE_PHP', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (33, 13, 'GLUE(Nodejs)', 'GLUE_NODEJS', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (34, 13, 'GLUE(PowerShell)', 'GLUE_POWERSHELL', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);

INSERT INTO `sys_dict_detail` VALUES (35, 14, '第一个', 'FIRST', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (36, 14, '最后一个', 'LAST', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (37, 14, '轮询', 'ROUND', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (38, 14, '随机', 'RANDOM', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (39, 14, '一致性HASH', 'CONSISTENT_HASH', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (40, 14, '最不经常使用', 'LEAST_FREQUENTLY_USED', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (41, 14, '最近最久未使用', 'LEAST_RECENTLY_USED', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (42, 14, '故障转移', 'FAILOVER', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (43, 14, '忙碌转移', 'BUSYOVER', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (44, 14, '分片广播', 'SHARDING_BROADCAST', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);

INSERT INTO `sys_dict_detail` VALUES (45, 15, '顺序执行', 'SERIAL_EXECUTION', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (46, 15, '丢弃之后', 'DISCARD_LATER', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);
INSERT INTO `sys_dict_detail` VALUES (47, 15, '丢弃之前', 'COVER_EARLY', 1, NULL, NULL, '2021-05-07 14:28:00', NULL);

INSERT INTO `sys_dict_detail` VALUES (1, 1, '激活', 'true', 1, NULL, NULL, '2019-10-27 20:31:36', NULL);
INSERT INTO `sys_dict_detail` VALUES (2, 1, '禁用', 'false', 2, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_detail` VALUES (3, 4, '启用', 'true', 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_detail` VALUES (4, 4, '停用', 'false', 2, NULL, NULL, '2019-10-27 20:31:36', NULL);
INSERT INTO `sys_dict_detail` VALUES (5, 5, '启用', 'true', 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_detail` VALUES (6, 5, '停用', 'false', 2, NULL, NULL, '2019-10-27 20:31:36', NULL);
COMMIT;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
BEGIN;
INSERT INTO `sys_job` VALUES (8, '人事专员', b'1', 3, NULL, NULL, '2019-03-29 14:52:28', NULL);
INSERT INTO `sys_job` VALUES (10, '产品经理', b'1', 4, NULL, NULL, '2019-03-29 14:55:51', NULL);
INSERT INTO `sys_job` VALUES (11, '全栈开发', b'1', 2, NULL, 'admin', '2019-03-31 13:39:30', '2020-05-05 11:33:43');
INSERT INTO `sys_job` VALUES (12, '软件测试', b'1', 5, NULL, 'admin', '2019-03-31 13:39:43', '2020-05-10 19:56:26');
COMMIT;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (1, NULL, 7, 0, '系统管理', NULL, NULL, 1, 'system', 'system', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-18 15:11:29', NULL);
INSERT INTO `sys_menu` VALUES (2, 1, 3, 1, '用户管理', 'User', 'system/user/Index.vue', 2, 'peoples', 'user', b'0', b'0', b'0', 'user:list', NULL, NULL, '2018-12-18 15:14:44', NULL);
INSERT INTO `sys_menu` VALUES (44, 2, 0, 2, '用户新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'user:add', NULL, NULL, '2019-10-29 10:59:46', NULL);
INSERT INTO `sys_menu` VALUES (45, 2, 0, 2, '用户编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'user:edit', NULL, NULL, '2019-10-29 11:00:08', NULL);
INSERT INTO `sys_menu` VALUES (46, 2, 0, 2, '用户删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'user:del', NULL, NULL, '2019-10-29 11:00:23', NULL);
INSERT INTO `sys_menu` VALUES (3, 1, 3, 1, '角色管理', 'Role', 'system/role/Index.vue', 3, 'role', 'role', b'0', b'0', b'0', 'roles:list', NULL, NULL, '2018-12-18 15:16:07', NULL);
INSERT INTO `sys_menu` VALUES (48, 3, 0, 2, '角色创建', NULL, '', 2, '', '', b'0', b'0', b'0', 'roles:add', NULL, NULL, '2019-10-29 12:45:34', NULL);
INSERT INTO `sys_menu` VALUES (49, 3, 0, 2, '角色修改', NULL, '', 3, '', '', b'0', b'0', b'0', 'roles:edit', NULL, NULL, '2019-10-29 12:46:16', NULL);
INSERT INTO `sys_menu` VALUES (50, 3, 0, 2, '角色删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'roles:del', NULL, NULL, '2019-10-29 12:46:51', NULL);
INSERT INTO `sys_menu` VALUES (5, 1, 3, 1, '菜单管理', 'Menu', 'system/menu/Index.vue', 5, 'menu', 'menu', b'0', b'0', b'0', 'menu:list', NULL, NULL, '2018-12-18 15:17:28', NULL);
INSERT INTO `sys_menu` VALUES (52, 5, 0, 2, '菜单新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'menu:add', NULL, NULL, '2019-10-29 12:55:07', NULL);
INSERT INTO `sys_menu` VALUES (53, 5, 0, 2, '菜单编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'menu:edit', NULL, NULL, '2019-10-29 12:55:40', NULL);
INSERT INTO `sys_menu` VALUES (54, 5, 0, 2, '菜单删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'menu:del', NULL, NULL, '2019-10-29 12:56:00', NULL);
INSERT INTO `sys_menu` VALUES (35, 1, 3, 1, '部门管理', 'Dept', 'system/dept/Index.vue', 6, 'dept', 'dept', b'0', b'0', b'0', 'dept:list', NULL, NULL, '2019-03-25 09:46:00', NULL);
INSERT INTO `sys_menu` VALUES (56, 35, 0, 2, '部门新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'dept:add', NULL, NULL, '2019-10-29 12:57:09', NULL);
INSERT INTO `sys_menu` VALUES (57, 35, 0, 2, '部门编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'dept:edit', NULL, NULL, '2019-10-29 12:57:27', NULL);
INSERT INTO `sys_menu` VALUES (58, 35, 0, 2, '部门删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'dept:del', NULL, NULL, '2019-10-29 12:57:41', NULL);
INSERT INTO `sys_menu` VALUES (37, 1, 3, 1, '岗位管理', 'Jo', 'system/job/Index.vue', 7, 'Steve-Jobs', 'jo', b'0', b'0', b'0', 'job:list', NULL, NULL, '2019-03-29 13:51:18', NULL);
INSERT INTO `sys_menu` VALUES (60, 37, 0, 2, '岗位新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'job:add', NULL, NULL, '2019-10-29 12:58:27', NULL);
INSERT INTO `sys_menu` VALUES (61, 37, 0, 2, '岗位编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'job:edit', NULL, NULL, '2019-10-29 12:58:45', NULL);
INSERT INTO `sys_menu` VALUES (62, 37, 0, 2, '岗位删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'job:del', NULL, NULL, '2019-10-29 12:59:04', NULL);
INSERT INTO `sys_menu` VALUES (39, 1, 3, 1, '字典管理', 'Dict', 'system/dict/Index.vue', 8, 'dictionary', 'dict', b'0', b'0', b'0', 'dict:list', NULL, NULL, '2019-04-10 11:49:04', NULL);
INSERT INTO `sys_menu` VALUES (64, 39, 0, 2, '字典新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'dict:add', NULL, NULL, '2019-10-29 13:00:17', NULL);
INSERT INTO `sys_menu` VALUES (65, 39, 0, 2, '字典编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'dict:edit', NULL, NULL, '2019-10-29 13:00:42', NULL);
INSERT INTO `sys_menu` VALUES (66, 39, 0, 2, '字典删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'dict:del', NULL, NULL, '2019-10-29 13:00:59', NULL);

INSERT INTO `sys_menu` VALUES (6, NULL, 5, 0, '系统监控', NULL, NULL, 10, 'monitor', 'monitor', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-18 15:17:48', NULL);
INSERT INTO `sys_menu` VALUES (7, 6, 0, 1, '操作日志', 'Log', 'monitor/log/Index.vue', 11, 'log', 'logs', b'0', b'1', b'0', NULL, NULL, 'admin', '2018-12-18 15:18:26', '2020-06-06 13:11:57');
INSERT INTO `sys_menu` VALUES (32, 6, 0, 1, '异常日志', 'ErrorLog', 'monitor/log/ErrorLog.vue', 12, 'error', 'errorLog', b'0', b'0', b'0', NULL, NULL, NULL, '2019-01-13 13:49:03', NULL);
INSERT INTO `sys_menu` VALUES (41, 6, 0, 1, '在线用户', 'OnlineUser', 'monitor/online/Index.vue', 10, 'Steve-Jobs', 'online', b'0', b'0', b'0', NULL, NULL, NULL, '2019-10-26 22:08:43', NULL);

INSERT INTO `sys_menu` VALUES (10, NULL, 5, 0, '组件管理', NULL, NULL, 50, 'zujian', 'components', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-19 13:38:16', NULL);
INSERT INTO `sys_menu` VALUES (11, 10, 0, 1, '图标库', 'Icons', 'components/icons/Index.vue', 51, 'icon', 'icon', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-19 13:38:49', NULL);
INSERT INTO `sys_menu` VALUES (15, 10, 0, 1, '富文本', 'Editor', 'components/Editor.vue', 52, 'fw', 'tinymce', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-27 11:58:25', NULL);
INSERT INTO `sys_menu` VALUES (33, 10, 0, 1, 'Markdown', 'Markdown', 'components/MarkDown.vue', 53, 'markdown', 'markdown', b'0', b'0', b'0', NULL, NULL, NULL, '2019-03-08 13:46:44', NULL);
INSERT INTO `sys_menu` VALUES (83, 10, 0, 1, '图表库', 'Echarts', 'components/Echarts.vue', 50, 'chart', 'echarts', b'0', b'1', b'0', '', NULL, NULL, '2019-11-21 09:04:32', NULL);

INSERT INTO `sys_menu` VALUES (36, NULL, 7, 0, '系统工具', NULL, '', 30, 'sys-tools', 'sys-tools', b'0', b'0', b'0', NULL, NULL, NULL, '2019-03-29 10:57:35', NULL);
INSERT INTO `sys_menu` VALUES (14, 36, 0, 1, '邮件工具', 'Email', 'tools/email/Index.vue', 35, 'email', 'email', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-27 10:13:09', NULL);

INSERT INTO `sys_menu` VALUES (90, NULL, 3, 0, '调度管理', NULL, '', 50, 'zujian', 'job', b'0', b'0', b'0', NULL, NULL, NUll, '2021-05-07 14:00:00', NULL);
INSERT INTO `sys_menu` VALUES (91, 90, 0, 1, '执行器管理', 'Group', 'job/group/Index.vue', 51, 'timing', 'deviceInfo', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-27 10:13:09', NULL);
INSERT INTO `sys_menu` VALUES (92, 90, 0, 1, '任务管理', 'Task', 'job/info/Index.vue', 52, 'timing', 'task', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-27 10:13:09', NULL);
INSERT INTO `sys_menu` VALUES (93, 90, 0, 1, '任务日志', 'JobLog', 'job/log/Index.vue', 53, 'timing', 'jobLog', b'0', b'0', b'0', NULL, NULL, NULL, '2018-12-27 10:13:09', NULL);

COMMIT;


-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, '超级管理员', 1, '-', '自定义', NULL, 'admin', '2018-11-23 11:04:37', '2020-08-06 16:10:24');
INSERT INTO `sys_role` VALUES (2, '普通用户', 2, '-', '自定义', NULL, 'admin', '2018-11-23 13:09:06', '2020-09-05 10:45:12');
COMMIT;

-- ----------------------------
-- Records of sys_roles_depts
-- ----------------------------
BEGIN;
INSERT INTO `sys_roles_depts` VALUES (1, 7);
INSERT INTO `sys_roles_depts` VALUES (1, 8);
INSERT INTO `sys_roles_depts` VALUES (2, 6);
COMMIT;

-- ----------------------------
-- Records of sys_roles_menus
-- ----------------------------
BEGIN;
INSERT INTO `sys_roles_menus` VALUES (1, 1);
INSERT INTO `sys_roles_menus` VALUES (1, 2);
INSERT INTO `sys_roles_menus` VALUES (2, 1);
INSERT INTO `sys_roles_menus` VALUES (2, 2);
INSERT INTO `sys_roles_menus` VALUES (3, 1);
INSERT INTO `sys_roles_menus` VALUES (3, 2);
INSERT INTO `sys_roles_menus` VALUES (5, 1);
INSERT INTO `sys_roles_menus` VALUES (5, 2);
INSERT INTO `sys_roles_menus` VALUES (6, 1);
INSERT INTO `sys_roles_menus` VALUES (6, 2);
INSERT INTO `sys_roles_menus` VALUES (7, 1);
INSERT INTO `sys_roles_menus` VALUES (7, 2);
INSERT INTO `sys_roles_menus` VALUES (10, 1);
INSERT INTO `sys_roles_menus` VALUES (10, 2);
INSERT INTO `sys_roles_menus` VALUES (11, 1);
INSERT INTO `sys_roles_menus` VALUES (11, 2);
INSERT INTO `sys_roles_menus` VALUES (14, 1);
INSERT INTO `sys_roles_menus` VALUES (14, 2);
INSERT INTO `sys_roles_menus` VALUES (15, 1);
INSERT INTO `sys_roles_menus` VALUES (15, 2);
INSERT INTO `sys_roles_menus` VALUES (28, 1);
INSERT INTO `sys_roles_menus` VALUES (28, 2);
INSERT INTO `sys_roles_menus` VALUES (32, 1);
INSERT INTO `sys_roles_menus` VALUES (35, 1);
INSERT INTO `sys_roles_menus` VALUES (35, 2);
INSERT INTO `sys_roles_menus` VALUES (36, 1);
INSERT INTO `sys_roles_menus` VALUES (36, 2);
INSERT INTO `sys_roles_menus` VALUES (37, 1);
INSERT INTO `sys_roles_menus` VALUES (37, 2);
INSERT INTO `sys_roles_menus` VALUES (39, 1);
INSERT INTO `sys_roles_menus` VALUES (39, 2);
INSERT INTO `sys_roles_menus` VALUES (41, 1);
INSERT INTO `sys_roles_menus` VALUES (44, 1);
INSERT INTO `sys_roles_menus` VALUES (45, 1);
INSERT INTO `sys_roles_menus` VALUES (46, 1);
INSERT INTO `sys_roles_menus` VALUES (48, 1);
INSERT INTO `sys_roles_menus` VALUES (49, 1);
INSERT INTO `sys_roles_menus` VALUES (50, 1);
INSERT INTO `sys_roles_menus` VALUES (52, 1);
INSERT INTO `sys_roles_menus` VALUES (53, 1);
INSERT INTO `sys_roles_menus` VALUES (54, 1);
INSERT INTO `sys_roles_menus` VALUES (56, 1);
INSERT INTO `sys_roles_menus` VALUES (57, 1);
INSERT INTO `sys_roles_menus` VALUES (58, 1);
INSERT INTO `sys_roles_menus` VALUES (60, 1);
INSERT INTO `sys_roles_menus` VALUES (61, 1);
INSERT INTO `sys_roles_menus` VALUES (62, 1);
INSERT INTO `sys_roles_menus` VALUES (64, 1);
INSERT INTO `sys_roles_menus` VALUES (65, 1);
INSERT INTO `sys_roles_menus` VALUES (66, 1);
INSERT INTO `sys_roles_menus` VALUES (73, 1);
INSERT INTO `sys_roles_menus` VALUES (74, 1);
INSERT INTO `sys_roles_menus` VALUES (75, 1);
COMMIT;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 2, 'admin', '管理员', '男', '18888888888', 'admin@orvillex.com', 'avatar-20200806032259161.png', '/Users/jie/Documents/work/me/admin/eladmin/~/avatar/avatar-20200806032259161.png', '$2a$10$Egp1/gvFlt7zhlXVfEFw4OfWQCGPw0ClmMcc6FjTnvXNRVf9zdMRa', b'1', 1, NULL, 'admin', '2020-05-03 16:38:31', '2018-08-23 09:11:56', '2020-09-05 10:43:31');
INSERT INTO `sys_user` VALUES (2, 2, 'test', '测试', '男', '18888888888', '231@qq.com', NULL, NULL, '$2a$10$4XcyudOYTSz6fue6KFNMHeUQnCX5jbBQypLEnGk1PmekXt5c95JcK', b'0', 1, 'admin', 'admin', NULL, '2020-05-05 11:15:49', '2020-09-05 10:43:38');
COMMIT;

-- ----------------------------
-- Records of sys_users_jobs
-- ----------------------------
BEGIN;
INSERT INTO `sys_users_jobs` VALUES (1, 11);
INSERT INTO `sys_users_jobs` VALUES (2, 12);
COMMIT;

-- ----------------------------
-- Records of sys_users_roles
-- ----------------------------
BEGIN;
INSERT INTO `sys_users_roles` VALUES (1, 1);
INSERT INTO `sys_users_roles` VALUES (2, 2);
COMMIT;

-- ----------------------------
-- Records of sys_job_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_job_group`(`group_id`, `app_name`, `title`, `address_type`, `address_list`) VALUES (1, 'job-executor-sample1', '示例执行器1', 0, NULL);
INSERT INTO `sys_job_group`(`group_id`, `app_name`, `title`, `address_type`, `address_list`) VALUES (2, 'job-executor-sample2', '示例执行器2', 0, NULL);
COMMIT;

-- ----------------------------
-- Records of sys_job_info
-- ----------------------------
BEGIN;
INSERT INTO `sys_job_info`(`info_id`, `job_group`, `job_cron`, `job_desc`, `create_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_job_id`) VALUES (1, 1, '0 0 0 * * ? *', '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'admin', '', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO `sys_job_info`(`info_id`, `job_group`, `job_cron`, `job_desc`, `create_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_job_id`) VALUES (2, 1, '0 0 0 * * ? *', '测试任务2', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'admin', '', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'GLUE(Java)', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
COMMIT;
