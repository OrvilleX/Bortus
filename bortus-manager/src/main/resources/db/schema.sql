/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 100505
 Source Host           : localhost:3306
 Source Schema         : eladmin

 Target Server Type    : MySQL
 Target Server Version : 100505
 File Encoding         : 65001

 Date: 05/09/2020 10:49:19
*/

-- SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_wx_user
-- ----------------------------
CREATE TABLE `sys_wx_user` (
  `wxuser_id` int(11) NOT NULL AUTO_INCREMENT,
  `wx_open_id` varchar(512) NOT NULL COMMENT '微信OpenId',
  `phone_number` varchar(256) DEFAULT NULL COMMENT '手机号',
  `nick_name` varchar(256) NOT NULL COMMENT '昵称',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `email` varchar(256) DEFAULT NULL COMMENT '邮箱',
  `head_url` varchar(256) NOT NULL COMMENT '头像',
  `city` varchar(128) DEFAULT NULL COMMENT '城市',
  `province` varchar(128) DEFAULT NULL COMMENT '省份',
  `gender` int(11) DEFAULT NULL COMMENT '性别',
  `session_key` varchar(255) DEFAULT NULL COMMENT '会话令牌',
  `encrypted_data` varchar(512) DEFAULT NULL,
  `iv` varchar(512) DEFAULT NULL,
  `source` int(11) DEFAULT NULL COMMENT '用户来源',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`wxuser_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint(20) DEFAULT NULL COMMENT '上级部门',
  `sub_count` int(5) DEFAULT 0 COMMENT '子部门数目',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `dept_sort` int(5) DEFAULT 999 COMMENT '排序',
  `enabled` bit(1) NOT NULL COMMENT '状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`),
  KEY `inx_pid` (`pid`),
  KEY `inx_enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT '字典名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dict_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_dict_detail
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_detail`;
CREATE TABLE `sys_dict_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dict_id` bigint(11) DEFAULT NULL COMMENT '字典id',
  `label` varchar(255) NOT NULL COMMENT '字典标签',
  `value` varchar(255) NOT NULL COMMENT '字典值',
  `dict_sort` int(5) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`detail_id`),
  KEY `FK5tpkputc6d9nboxojdbgnpmyb` (`dict_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT '岗位名称',
  `enabled` bit(1) NOT NULL COMMENT '岗位状态',
  `job_sort` int(5) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `uniq_name` (`name`),
  KEY `job_inx_enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `description` varchar(255) DEFAULT NULL,
  `log_type` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `params` text DEFAULT NULL,
  `request_ip` varchar(255) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `browser` varchar(255) DEFAULT NULL,
  `exception_detail` text DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `log_create_time_index` (`create_time`),
  KEY `inx_log_type` (`log_type`)
) ENGINE=InnoDB AUTO_INCREMENT=3537 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint(20) DEFAULT NULL COMMENT '上级菜单ID',
  `sub_count` int(5) DEFAULT 0 COMMENT '子菜单数目',
  `type` int(11) DEFAULT NULL COMMENT '菜单类型',
  `title` varchar(255) DEFAULT NULL COMMENT '菜单标题',
  `name` varchar(255) DEFAULT NULL COMMENT '组件名称',
  `component` varchar(255) DEFAULT NULL COMMENT '组件',
  `menu_sort` int(5) DEFAULT NULL COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `path` varchar(255) DEFAULT NULL COMMENT '链接地址',
  `i_frame` bit(1) DEFAULT NULL COMMENT '是否外链',
  `cache` bit(1) DEFAULT b'0' COMMENT '缓存',
  `hidden` bit(1) DEFAULT b'0' COMMENT '隐藏',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `uniq_title` (`title`),
  UNIQUE KEY `uniq_menu_name` (`name`),
  KEY `inx_menu_pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `level` int(255) DEFAULT NULL COMMENT '角色级别',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `data_scope` varchar(255) DEFAULT NULL COMMENT '数据权限',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uniq_role_name` (`name`),
  KEY `role_name_index` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_roles_depts
-- ----------------------------
DROP TABLE IF EXISTS `sys_roles_depts`;
CREATE TABLE `sys_roles_depts` (
  `role_id` bigint(20) NOT NULL,
  `dept_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`dept_id`),
  KEY `FK7qg6itn5ajdoa9h9o78v9ksur` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_roles_menus
-- ----------------------------
DROP TABLE IF EXISTS `sys_roles_menus`;
CREATE TABLE `sys_roles_menus` (
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`menu_id`,`role_id`),
  KEY `FKcngg2qadojhi3a651a5adkvbq` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门名称',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
  `gender` varchar(2) DEFAULT NULL COMMENT '性别',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `avatar_name` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `avatar_path` varchar(255) DEFAULT NULL COMMENT '头像真实路径',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `is_admin` bit(1) DEFAULT b'0' COMMENT '是否为admin账号',
  `enabled` bigint(20) DEFAULT NULL COMMENT '状态：1启用、0禁用',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新着',
  `pwd_reset_time` datetime DEFAULT NULL COMMENT '修改密码的时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_kpubos9gc2cvtkb0thktkbkes` (`email`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `uniq_username` (`username`),
  UNIQUE KEY `uniq_email` (`email`),
  KEY `FK5rwmryny6jthaaxkogownknqp` (`dept_id`),
  KEY `FKpq2dhypk2qgt68nauh2by22jb` (`avatar_name`),
  KEY `inx_user_enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_users_jobs
-- ----------------------------
DROP TABLE IF EXISTS `sys_users_jobs`;
CREATE TABLE `sys_users_jobs` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `job_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_users_roles
-- ----------------------------
DROP TABLE IF EXISTS `sys_users_roles`;
CREATE TABLE `sys_users_roles` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKq4eq273l04bpu4efj0jd0jb98` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for tool_email_config
-- ----------------------------
DROP TABLE IF EXISTS `tool_email_config`;
CREATE TABLE `tool_email_config` (
  `config_id` bigint(20) NOT NULL COMMENT 'ID',
  `from_user` varchar(255) DEFAULT NULL COMMENT '收件人',
  `host` varchar(255) DEFAULT NULL COMMENT '邮件服务器SMTP地址',
  `pass` varchar(255) DEFAULT NULL COMMENT '密码',
  `port` varchar(255) DEFAULT NULL COMMENT '端口',
  `user` varchar(255) DEFAULT NULL COMMENT '发件者用户名',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_info
-- ----------------------------
CREATE TABLE `sys_job_info` (
  `info_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
  `job_desc` varchar(255) NOT NULL,
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_job_id` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新着',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
CREATE TABLE `sys_job_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
  `trigger_msg` text COMMENT '调度-日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int(11) NOT NULL COMMENT '执行-状态',
  `handle_msg` text COMMENT '执行-日志',
  `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`log_id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_logreport
-- ----------------------------
CREATE TABLE `sys_job_logreport` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
  `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
  `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
  `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_trigger_day` (`trigger_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_logglue
-- ----------------------------
CREATE TABLE `sys_job_logglue` (
  `logglue_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`logglue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_registry
-- ----------------------------
CREATE TABLE `sys_job_registry` (
  `registry_id` int(11) NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(50) NOT NULL,
  `registry_key` varchar(255) NOT NULL,
  `registry_value` varchar(255) NOT NULL,
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`registry_id`),
  KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for sys_job_group
-- ----------------------------
CREATE TABLE `sys_job_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(64) NOT NULL COMMENT '执行器名称',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


-- ----------------------------
-- Table structure for cmm_favorites
-- ----------------------------
CREATE TABLE `cmm_favorites` (
  `fav_id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL COMMENT '会员编号',
  `target_id` int(11) NOT NULL COMMENT '收藏编号',
  `target_type` varchar(128) NOT NULL COMMENT '收藏类型',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`fav_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for member_info
-- ----------------------------
CREATE TABLE `member_info` (
  `member_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL COMMENT '用户主键',
  `wx_open_id` varchar(128) NOT NULL COMMENT '微信标识',
  `level` int(11) NOT NULL COMMENT '会员等级',
  `experience` int(11) NOT NULL COMMENT '当前经验',
  `qrcode` varchar(128) NOT NULL COMMENT '会员码',
  `integral` int(11) NOT NULL DEFAULT '0' COMMENT '积分总值',
  `brithday` date DEFAULT NULL COMMENT '生日',
  `mailing_address` varchar(255) DEFAULT NULL COMMENT '邮寄地址',
  `gender` varchar(4) DEFAULT NULL COMMENT '性别',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for member_integral
-- ----------------------------
CREATE TABLE `member_integral` (
  `integral_id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL COMMENT '会员编号',
  `value` decimal(18,2) NOT NULL COMMENT '积分值',
  `state` varchar(128) NOT NULL COMMENT '状态',
  `remark` varchar(255) DEFAULT NULL COMMENT '说明',
  `target_id` int(11) DEFAULT NULL COMMENT '消费目标编号',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`integral_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for cmm_notice
-- ----------------------------
CREATE TABLE `cmm_notice` (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(128) NOT NULL COMMENT '内容',
  `type` varchar(128) NOT NULL COMMENT '通告类型',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转路径',
  `target_id` int(11) NOT NULL COMMENT '所属目标',
  `target_type` varchar(128) NOT NULL COMMENT '所属类型',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for member_coupon 
-- ----------------------------
CREATE TABLE `member_coupon` (
  `coupon_id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL COMMENT '会员主键',
  `type` varchar(128) NOT NULL COMMENT '折扣目标',
  `min_price` decimal(18,2) DEFAULT NULL COMMENT '最低金额',
  `price` decimal(18,2) DEFAULT NULL COMMENT '折扣金额',
  `discount` decimal(18,2) DEFAULT NULL COMMENT '折扣',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `state` varchar(128) NOT NULL COMMENT '状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for member_integral_goods 
-- ----------------------------
CREATE TABLE `member_integral_goods` (
  `goods_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '商品名称',
  `price` decimal(18,2) NOT NULL COMMENT '商品积分',
  `main_pic` varchar(255) NOT NULL COMMENT '商品主图',
  `purchase` int(11) NOT NULL DEFAULT '0' COMMENT '购买次数',
  `surplus` int(11) NOT NULL DEFAULT '0' COMMENT '剩余数量',
  `description` varchar(512) NOT NULL COMMENT '说明',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for cmm_comment 
-- ----------------------------
CREATE TABLE `cmm_comment` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL COMMENT '会员主键',
  `shop_id` int(11) DEFAULT NULL COMMENT '店铺主键',
  `activity_id` int(11) DEFAULT NULL COMMENT '漫展主键',
  `comment` varchar(512) NOT NULL COMMENT '评论内容',
  `main_pic` varchar(255) DEFAULT NULL COMMENT '评论主图',
  `second_pic` varchar(255) DEFAULT NULL COMMENT '评论次图',
  `thrid_pic` varchar(255) DEFAULT NULL COMMENT '评论尾图',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT '评论权重',
  `nick_name` varchar(128) NOT NULL COMMENT '会员昵称',
  `head_url` varchar(255) NOT NULL COMMENT '会员头像',
  `is_show` bit(1) DEFAULT b'0' COMMENT '是否可看',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for cmm_pay 
-- ----------------------------
CREATE TABLE `cmm_pay` (
  `pay_id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(125) NOT NULL COMMENT '支付方式',
  `price` decimal(18,2) NOT NULL COMMENT '支付金额',
  `pre_id` varchar(125) DEFAULT NULL COMMENT '预支付单',
  `state` varchar(125) NOT NULL COMMENT '支付状态',
  `sync_state` varchar(125) DEFAULT NULL COMMENT '同步状态',
  `third_order_id` varchar(125) DEFAULT NULL COMMENT '三方订单号',
  `third_order_remark` varchar(256) DEFAULT NULL COMMENT '三方备注',
  `third_callback` bit(1) NOT NULL DEFAULT b'0' COMMENT '三方回调',
  `commodity_id` int(11) NOT NULL COMMENT '商品编号',
  `commodity_type` varchar(125) NOT NULL COMMENT '商品类型',
  `is_refund` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为退款',
  `refund_state` varchar(125) DEFAULT NULL COMMENT '退款状态',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`pay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for cmm_shop_info 
-- ----------------------------
CREATE TABLE `cmm_shop_info` (
  `shop_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '名称',
  `main_pic` varchar(255) NOT NULL COMMENT '主图',
  `per_consumption` decimal(18,2) NOT NULL DEFAULT '0' COMMENT '人均消费',
  `grade` decimal(5, 1) NOT NULL DEFAULT '0' COMMENT '评分',
  `latitude` decimal(10, 7) NOT NULL COMMENT '精度',
  `longitude` decimal(10, 7) NOT NULL COMMENT '纬度',
  `address` varchar(255) NOT NULL COMMENT '地址',
  `phone` varchar(128) NOT NULL COMMENT '电话',
  `opening_hours` varchar(128) NOT NULL COMMENT '营业时间',
  `description` varchar(512) DEFAULT NULL COMMENT '店铺说明',
  `isshow` bit(1) DEFAULT b'0' COMMENT '是否显示',
  `code` varchar(128) NOT NULL COMMENT '店铺编号',
  `lowest_box` int(11) NOT NULL DEFAULT '0' COMMENT '包厢最低人数',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for shop_dishes_info 
-- ----------------------------
CREATE TABLE `shop_dishes_info` (
  `dishes_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺编号',
  `name` varchar(128) NOT NULL COMMENT '菜品名称',
  `price` decimal(18,2) NOT NULL COMMENT '菜品价格',
  `is_home_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否首页显示',
  `home_weight` int(11) NOT NULL DEFAULT '0' COMMENT '首页显示权重',
  `is_shop_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否店铺首页显示',
  `shop_weight` int(11) NOT NULL DEFAULT '0' COMMENT '店铺首页显示权重',
  `is_star` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为明星菜品',
  `sales_volume` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `description` varchar(512) DEFAULT NULL COMMENT '介绍',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '主图',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for shop_activity_info 
-- ----------------------------
CREATE TABLE `shop_activity_info` (
  `activity_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺主键',
  `name` varchar(128) NOT NULL COMMENT '活动名称',
  `price` decimal(18,2) NOT NULL COMMENT '活动价格',
  `small_pic` varchar(255) NOT NULL COMMENT '活动微缩图',
  `main_pic` varchar(255) NOT NULL COMMENT '活动主图',
  `second_pic` varchar(255) DEFAULT NULL COMMENT '活动次图',
  `thrid_pic` varchar(255) DEFAULT NULL COMMENT '活动尾图',
  `is_show` bit(1) DEFAULT b'0' COMMENT '是否显示',
  `description` varchar(512) NOT NULL COMMENT '活动说明',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for shop_activity_time 
-- ----------------------------
CREATE TABLE `shop_activity_time` (
  `time_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL COMMENT '开始时段',
  `end_time` datetime NOT NULL COMMENT '结束时段',
  `member` int(11) NOT NULL COMMENT '可预约人数',
  `reserved` int(11) NOT NULL DEFAULT '0' COMMENT '已预约人数',
  `time` varchar(128) NOT NULL COMMENT '时段字符',
  `activity_id` int(11) NOT NULL COMMENT '店铺活动主键',
  `price` decimal(18,2) NOT NULL COMMENT '单价',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`time_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for shop_activity_reserved 
-- ----------------------------
CREATE TABLE `shop_activity_reserved` (
  `areserved_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺编号',
  `member_id` int(11) NOT NULL COMMENT '会员编号',
  `time_id` int(11) NOT NULL COMMENT '时段主键',
  `time` varchar(128) DEFAULT NULL COMMENT '时段信息',
  `activity_id` int(11) NOT NULL COMMENT '活动编号',
  `state` varchar(128) NOT NULL COMMENT '预约状态',
  `amount` decimal(18,2) NOT NULL COMMENT '实付价格',
  `amount_payable` decimal(18,2) NOT NULL COMMENT '应付价格',
  `pay_id` int(11) DEFAULT NULL COMMENT '支付单号',
  `member` int(11) NOT NULL COMMENT '预约人数',
  `remark` varchar(512) DEFAULT NULL COMMENT '注释',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`areserved_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for shop_table_reserved 
-- ----------------------------
CREATE TABLE `shop_table_reserved` (
  `table_id` int(11) NOT NULL AUTO_INCREMENT,
  `personnel` int(11) NOT NULL COMMENT '人数',
  `time` datetime NOT NULL COMMENT '预约时间',
  `type` varchar(128) NOT NULL COMMENT '预约类型',
  `name` varchar(128) NOT NULL COMMENT '预约姓名',
  `phone` varchar(128) NOT NULL COMMENT '预约号码',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注说明',
  `shop_id` int(11) NOT NULL COMMENT '店铺编号',
  `member_id` int(11) NOT NULL COMMENT '会员编号',
  `state` varchar(128) NOT NULL COMMENT '预约状态',
  `cause` varchar(128) DEFAULT NULL COMMENT '原因',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_activity 
-- ----------------------------
CREATE TABLE `comiket_activity` (
  `comiket_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '名称',
  `small_pic` varchar(255) NOT NULL COMMENT '微缩图',
  `online_price` decimal(18,2) NOT NULL COMMENT '线上单价',
  `price` decimal(18,2) NOT NULL COMMENT '线下单价',
  `visited` int(11) NOT NULL DEFAULT '0' COMMENT '查看次数',
  `star` int(11) NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `state` varchar(128) NOT NULL COMMENT '状态',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `description` varchar(512) DEFAULT NULL COMMENT '活动介绍',
  `latitude` decimal(10, 7) NOT NULL COMMENT '经度',
  `longitude` decimal(10, 7) NOT NULL COMMENT '纬度',
  `address` varchar(255) NOT NULL COMMENT '地址信息',
  `main_pic` varchar(255) NOT NULL COMMENT '主图地址',
  `second_pic` varchar(255) DEFAULT NULL COMMENT '次图地址',
  `third_pic` varchar(255) DEFAULT NULL COMMENT '三图地址',
  `forth_pic` varchar(255) DEFAULT NULL COMMENT '四图地址',
  `has_booth_owner` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否存在摊主',
  `ticket_description` varchar(512) DEFAULT NULL COMMENT '票类说明',
  `booth_onwer_pic` varchar(255) DEFAULT NULL COMMENT '摊位图片',
  `booth_onwer_description` varchar(512) DEFAULT NULL COMMENT '摊位说明',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`comiket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_guest 
-- ----------------------------
CREATE TABLE `comiket_guest` (
  `guest_id` int(11) NOT NULL AUTO_INCREMENT,
  `comiket_id` int(11) NOT NULL COMMENT '漫展主键',
  `name` varchar(128) NOT NULL COMMENT '嘉宾名称',
  `head_url` varchar(255) NOT NULL COMMENT '嘉宾头像',
  `description` varchar(512) NOT NULL COMMENT '嘉宾信息',
  `index` int(11) NOT NULL DEFAULT '0' COMMENT '嘉宾顺序',
  `booth_onwer_description` varchar(512) DEFAULT NULL COMMENT '摊位说明',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`guest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_booth_owner 
-- ----------------------------
CREATE TABLE `comiket_booth_owner` (
  `booth_id` int(11) NOT NULL AUTO_INCREMENT,
  `comiket_id` int(11) NOT NULL COMMENT '漫展主键',
  `name` varchar(128) NOT NULL COMMENT '摊位名',
  `price` decimal(18,2) NOT NULL COMMENT '价格',
  `state` varchar(128) NOT NULL COMMENT '摊位状态',
  `group_id` int(11) NOT NULL COMMENT '分组编号',
  `group_name` varchar(128) NOT NULL COMMENT '分组名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`booth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_ticket 
-- ----------------------------
CREATE TABLE `comiket_ticket` (
  `ticket_id` int(11) NOT NULL AUTO_INCREMENT,
  `comiket_id` int(11) NOT NULL COMMENT '漫展主键',
  `name` varchar(128) NOT NULL COMMENT '票名',
  `price` decimal(18,2) NOT NULL COMMENT '票价',
  `count` int(11) NOT NULL COMMENT '数量',
  `start_time` datetime NOT NULL COMMENT '可购买时间',
  `end_time` datetime NOT NULL COMMENT '不可购买时间',
  `group_id` int(11) NOT NULL COMMENT '票组编号',
  `group_name` varchar(128) NOT NULL COMMENT '票组名称',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_order 
-- ----------------------------
CREATE TABLE `comiket_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `comiket_id` int(11) NOT NULL COMMENT '漫展主键',
  `member_id` int(11) NOT NULL COMMENT '会员主键',
  `amount` decimal(18,2) NOT NULL COMMENT '实付单价',
  `amount_payable` decimal(18,2) NOT NULL COMMENT '应付单价',
  `state` varchar(128) NOT NULL COMMENT '订单状态',
  `pay_id` varchar(128) DEFAULT NULL COMMENT '支付单号',
  `expire_time` datetime NOT NULL COMMENT '失效时间',
  `type` varchar(128) NOT NULL COMMENT '购票种类（票/摊）',
  `coupon_id` int(11) DEFAULT NULL COMMENT '优惠券编号',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for comiket_order_detail 
-- ----------------------------
CREATE TABLE `comiket_order_detail` (
  `detail_id` int(11) NOT NULL AUTO_INCREMENT,
  `comiket_id` int(11) NOT NULL COMMENT '漫展主键',
  `member_id` int(11) NOT NULL COMMENT '会员编号',
  `order_id` int(11) DEFAULT NULL COMMENT '订单主键',
  `price` decimal(18,2) NOT NULL COMMENT '价格',
  `ticket_id` int(11) DEFAULT NULL COMMENT '票类主键',
  `booth_owner_id` int(11) DEFAULT NULL COMMENT '摊位主键',
  `name` varchar(128) NOT NULL COMMENT '票名',
  `state` varchar(128) NOT NULL COMMENT '核销状态',
  `count` int(11) NOT NULL COMMENT '购买数量',
  `write_off` int(11) NOT NULL DEFAULT '0' COMMENT '核销数量',
  `total_price` decimal(18,2) NOT NULL COMMENT '总价',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
SET FOREIGN_KEY_CHECKS = 1;
