ALTER TABLE `pet_favorite`
ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除' AFTER `update_time`;
