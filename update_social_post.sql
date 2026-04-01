-- 添加 images 字段存储多张图片（逗号分隔）
ALTER TABLE social_post ADD COLUMN images TEXT COMMENT '多张图片URL，逗号分隔';

-- 添加 videos 字段存储多个视频（逗号分隔）
ALTER TABLE social_post ADD COLUMN videos TEXT COMMENT '多个视频URL，逗号分隔';

-- 添加 tags 字段存储标签（逗号分隔）
ALTER TABLE social_post ADD COLUMN tags TEXT COMMENT '标签，逗号分隔';

-- 迁移现有 image 数据到 images
UPDATE social_post SET images = image WHERE image IS NOT NULL AND image != '';

-- 迁移现有 video 数据到 videos
UPDATE social_post SET videos = video WHERE video IS NOT NULL AND video != '';
