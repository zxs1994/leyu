-- ----------------------------
-- Table structure for sys__user
-- ----------------------------
DROP TABLE IF EXISTS `sys__user`;
CREATE TABLE `sys__user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` VARCHAR(255) NOT NULL COMMENT '邮箱',
  `name` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `token_version` INT NOT NULL DEFAULT 0 COMMENT 'token版本',
  `source` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--用户表';

-- ----------------------------
-- Table structure for sys__role
-- ----------------------------
DROP TABLE IF EXISTS `sys__role`;
CREATE TABLE `sys__role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `source` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--角色表';


-- ----------------------------
-- Table structure for sys__permission
-- ----------------------------
DROP TABLE IF EXISTS `sys__permission`;
CREATE TABLE `sys__permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名',
  `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
  `method` VARCHAR(50) NOT NULL COMMENT '请求方式',
  `path` VARCHAR(100) NOT NULL COMMENT '接口路径',
  `module` VARCHAR(50) NOT NULL COMMENT '权限模块',
  `module_name` VARCHAR(50) NOT NULL COMMENT '权限模块名称',
  `auth_level` INT NOT NULL DEFAULT 0 COMMENT '访问级别：0权限校验 1白名单',
  `parent_id` BIGINT NULL COMMENT '父权限ID',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   --  为了区别于 deleted SysPermissionScanner(系统权限扫描器)使用
  `del` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
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
  `source` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `source` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '数据来源：SYSTEM=系统内置，USER=用户创建',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统--角色-权限关联表';


-- ----------------------------
-- 初始化系统数据
-- ----------------------------
INSERT INTO `sys__user` (`email`, `name`, `password`) VALUES ('admin@qq.com', 'admin', '$2a$10$0.RvF.iEnw4grHb.WkAfdOi7qeKPyIfXDIAtrlZZk6QtfCNsRugMO');
INSERT INTO `sys__role` (`name`, `code`) VALUES ('超级管理员', 'SUPER_ADMIN');
INSERT INTO `sys__user_role` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `sys__permission` (`name`, `code`, `method`, `path`, `module`, `module_name`) VALUES("全局模块", "ALL", "*", "*", "ALL", "全局");
INSERT INTO `sys__role_permission` (`role_id`, `permission_id`) VALUES (1, 1);

