-- ----------------------------
-- Database: demo
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `job_id` bigint DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Sample data for user
-- ----------------------------
INSERT INTO `user` (`age`, `email`, `name`, `job_id`, `created_at`, `updated_at`, `deleted`) VALUES
(25, 'alice@example.com', 'Alice', 101, NOW(), NOW(), 0),
(30, 'bob@example.com', 'Bob', 102, NOW(), NOW(), 0),
(28, 'carol@example.com', 'Carol', 103, NOW(), NOW(), 0);

-- ----------------------------
-- Query examples
-- ----------------------------
---- 查询所有用户
--SELECT * FROM `user`;
--
---- 根据 ID 查询
--SELECT * FROM `user` WHERE `id` = 1;
--
---- 更新用户
--UPDATE `user` SET `age` = 26 WHERE `id` = 1;
--
---- 删除用户（逻辑删除示例）
--UPDATE `user` SET `deleted` = 1 WHERE `id` = 2;