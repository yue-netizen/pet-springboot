-- 给 social_post 表添加 title 字段
ALTER TABLE social_post ADD COLUMN title VARCHAR(100) COMMENT '帖子标题' AFTER user_id;
