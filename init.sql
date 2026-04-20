-- ----------------------------
-- Table structure for sys__dept
-- ----------------------------
DROP TABLE IF EXISTS `sys__dept`;
CREATE TABLE `sys__dept` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父部门ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 / 公司ID（SaaS隔离）',
  `name` VARCHAR(64) NOT NULL COMMENT '部门名称',
  `code` VARCHAR(64) DEFAULT NULL COMMENT '部门编码',
  `path` VARCHAR(255) NOT NULL COMMENT '层级路径，如 /1/3/8',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `leader_id` BIGINT DEFAULT NULL COMMENT '负责人用户ID',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=停用',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_tenant` (`tenant_id`),
  KEY `idx_path` (`path`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--组织部门表';


-- ----------------------------
-- Table structure for sys__user
-- ----------------------------
DROP TABLE IF EXISTS `sys__user`;
CREATE TABLE `sys__user` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户 / 公司ID（SaaS隔离）',
  `email` VARCHAR(255) NOT NULL COMMENT '邮箱',
  `name` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `token_version` INT NOT NULL DEFAULT 0 COMMENT 'token版本',
  `refresh_token` VARCHAR(255) DEFAULT NULL COMMENT '刷新token(哈希)',
  `refresh_expire_at` TIMESTAMP NULL DEFAULT NULL COMMENT '刷新token过期时间',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `source` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=停用',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_sys__user_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--用户表';


-- ----------------------------
-- Table structure for sys__role
-- ----------------------------
DROP TABLE IF EXISTS `sys__role`;
CREATE TABLE `sys__role` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户 / 公司ID（SaaS隔离）',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名',
  `code` VARCHAR(50) COMMENT '角色编码',
  `data_scope` VARCHAR(50) NOT NULL DEFAULT 'ALL' COMMENT '数据权限范围：DataScopeType枚举',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `source` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys__role_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--角色表';


-- ----------------------------
-- Table structure for sys__permission
-- ----------------------------
DROP TABLE IF EXISTS `sys__permission`;
CREATE TABLE `sys__permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT NULL COMMENT '父权限ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名',
  `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
  `method` VARCHAR(50) NOT NULL COMMENT '请求方式',
  `path` VARCHAR(100) NOT NULL COMMENT '接口路径',
  `module_name` VARCHAR(50) NOT NULL COMMENT '权限模块名称',
  `auth_level` INT NOT NULL DEFAULT 0 COMMENT '访问级别：0权限校验 1白名单 2登录即可',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：1=启用，0=停用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--权限表';


-- ----------------------------
-- Table structure for sys__user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys__user_role`;
CREATE TABLE `sys__user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `source` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--用户-角色关联表';


-- ----------------------------
-- Table structure for sys__role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys__role_permission`;
CREATE TABLE `sys__role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `source` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--角色-权限关联表';


-- ----------------------------
-- 初始化系统数据
-- ----------------------------

-- 租户 1 的部门
INSERT INTO `sys__dept` (`id`, `parent_id`, `tenant_id`, `name`, `code`, `path`, `sort`, `leader_id`)
VALUES
  (1, NULL, 1, '旭升的企业1', 'QY', '/1', 1, 2),
  (2, 1, 1, '研发部1', 'YF', '/1/2', 1, NULL),
  (3, 1, 1, '市场部1', 'SC', '/1/3', 2, NULL),
  (4, 2, 1, '前端组1', 'QD', '/1/2/4', 1, NULL),
  (5, 2, 1, '后端组1', 'HD', '/1/2/5', 2, NULL);

-- 租户 2 的部门
INSERT INTO `sys__dept` (`id`, `parent_id`, `tenant_id`, `name`, `code`, `path`, `sort`, `leader_id`)
VALUES
  (6, NULL, 6, '旭升的企业2', 'QY', '/6', 1, 3),
  (7, 6, 6, '研发部2', 'YF', '/6/7', 1, NULL),
  (8, 6, 6, '市场部2', 'SC', '/6/8', 2, NULL),
  (9, 7, 6, '前端组2', 'QD', '/6/7/9', 1, NULL),
  (10, 7, 6, '后端组2', 'HD', '/6/7/10', 2, NULL);

-- 平台用户
INSERT INTO `sys__user` (`id`, `email`, `name`, `password`, `source`) VALUES (1, 'admin@qq.com', 'admin', '$2a$10$0.RvF.iEnw4grHb.WkAfdOi7qeKPyIfXDIAtrlZZk6QtfCNsRugMO', 'SYSTEM');

-- 租户用户，属于租户 1
INSERT INTO `sys__user` (`id`, `dept_id`, `tenant_id`, `email`, `name`, `password`, `source`) VALUES (2, 1, 1, 'xusheng94@qq.com', '旭升1', '$2a$10$zIbKDqIoDB8U6NkolRYfE.f9W9/BajwuZ/6KdbqzK8LyU6SgUIhUu', 'SYSTEM');
-- 租户用户，属于租户 2
INSERT INTO `sys__user` (`id`, `dept_id`, `tenant_id`, `email`, `name`, `password`, `source`) VALUES (3, 6, 6, '695644578@qq.com', '旭升2', '$2a$10$zIbKDqIoDB8U6NkolRYfE.f9W9/BajwuZ/6KdbqzK8LyU6SgUIhUu', 'SYSTEM');

-- 全局共享角色
INSERT INTO `sys__role` (`id`, `name`, `code`, `source`) VALUES (1, '超级管理员', 'SUPER_ADMIN', 'SYSTEM'), (2, '观察者', 'OBSERVER', 'SYSTEM');

INSERT INTO `sys__permission` (`parent_id`, `name`, `code`, `method`, `path`, `module_name`) VALUES (null, '全局模块', 'ALL', '*', '/**', '全局'), (1, '全局查看', 'ALL_GET', 'GET', '/**', '全局');
INSERT INTO `sys__user_role` (`user_id`, `role_id`, `source`) VALUES (1, 1, 'SYSTEM'), (2, 1, 'SYSTEM'), (3, 1, 'SYSTEM');
INSERT INTO `sys__role_permission` (`role_id`, `permission_id`, `source`) VALUES (1, 1, 'SYSTEM'), (2, 2, 'SYSTEM');


-- ----------------------------
-- Table structure for data_scope_test 用来测试数据权限
-- ----------------------------
DROP TABLE IF EXISTS `data_scope_test`;
CREATE TABLE `data_scope_test` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户 / 公司ID（SaaS隔离）',
  `name` VARCHAR(50) NOT NULL COMMENT '名称',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `dept_id` BIGINT COMMENT '部门ID',
  `creator_id` BIGINT COMMENT '创建者ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_tenant (tenant_id),
  KEY idx_dept (dept_id),
  KEY idx_creator (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限测试表';