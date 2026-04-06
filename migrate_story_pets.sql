-- 迁移故事中的宠物数据到 pet_info 表
USE pet;

-- 插入故事相关的宠物到 pet_info 表（状态设为已领养=2）
INSERT INTO pet_info (name, breed, age, type, gender, image, description, health_status, status) VALUES
('洛奇', '混血狗', '2岁', 'dog', 'male', 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80', '曾经是一只胆小的小狗，在爪印之家康复后找到了新家', '健康', 2),
('雪球', '布偶猫', '1岁', 'cat', 'female', 'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&w=600&q=80', '温柔的布偶猫，性格安静优雅', '健康', 2),
('大黄', '中华田园犬', '3岁', 'dog', 'male', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80', '忠诚的中华田园犬，非常顾家', '健康', 2),
('咪咪', '橘猫', '8个月', 'cat', 'female', 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=600&q=80', '活泼可爱的小橘猫，喜欢撒娇', '健康', 2),
('旺财', '柯基混血', '1.5岁', 'dog', 'male', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=600&q=80', '短腿可爱，精力充沛', '健康', 2),
('花花', '三花猫', '2岁', 'cat', 'female', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80', '聪明伶俐的三花猫', '健康', 2),
('小黑', '拉布拉多', '2.5岁', 'dog', 'male', 'https://images.unsplash.com/photo-1544568100-847a948585b9?auto=format&fit=crop&w=600&q=80', '温顺友善的拉布拉多', '健康', 2),
('小白', '银渐层', '1岁', 'cat', 'female', 'https://images.unsplash.com/photo-1513245543132-31f507417b26?auto=format&fit=crop&w=600&q=80', '优雅的银渐层猫咪', '健康', 2);

-- 获取刚插入的宠物ID并更新 story_info 表
-- 假设 story_info 表中已经有8条记录，我们将它们与 pet_info 关联起来

-- 先查看当前 story_info 表的记录（方便确认ID）
-- SELECT id, title FROM story_info ORDER BY id;

-- 更新 story_info 表的 pet_id 字段
-- 注意：这里假设 story_info 表的 id 从 1 开始，并且 pet_info 新插入的 id 从 7 开始（因为init.sql中已经有6条宠物）
-- 如果实际情况不同，请调整下面的 UPDATE 语句

-- 使用变量和临时表来安全地更新
-- 方法1：如果知道 story_info 的 id 和对应的宠物名字
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '洛奇' LIMIT 1) WHERE title LIKE '%洛奇%' OR id = 1;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '雪球' LIMIT 1) WHERE title LIKE '%雪球%' OR id = 2;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '大黄' LIMIT 1) WHERE title LIKE '%大黄%' OR id = 3;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '咪咪' LIMIT 1) WHERE title LIKE '%咪咪%' OR id = 4;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '旺财' LIMIT 1) WHERE title LIKE '%旺财%' OR id = 5;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '花花' LIMIT 1) WHERE title LIKE '%花花%' OR id = 6;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '小黑' LIMIT 1) WHERE title LIKE '%小黑%' OR id = 7;
UPDATE story_info SET pet_id = (SELECT id FROM pet_info WHERE name = '小白' LIMIT 1) WHERE title LIKE '%小白%' OR id = 8;

-- 如果 story_info 表中没有数据，我们先插入一些示例故事
INSERT INTO story_info (user_id, pet_id, title, content, image, status) VALUES
(1, (SELECT id FROM pet_info WHERE name = '洛奇' LIMIT 1), '洛奇的新开始', '我们发现洛奇时，它只是一只胆小的小狗。爪印之家的团队确保它完全康复。把它带回家给我们带来了巨大的快乐。它不仅仅是宠物，它是我们的家人。', 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80', 1),
(1, (SELECT id FROM pet_info WHERE name = '雪球' LIMIT 1), '雪球的幸福生活', '雪球刚来的时候非常害羞，总是躲在沙发底下。经过几个月的相处，它现在已经是家里的小公主了，每天都要躺在我腿上睡觉。', 'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&w=600&q=80', 1),
(1, (SELECT id FROM pet_info WHERE name = '大黄' LIMIT 1), '大黄的忠诚', '大黄是一只中华田园犬，它非常忠诚。每天下班回家，它总是第一个在门口迎接我。有它在身边，我感到非常安心。', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80', 1),
(1, (SELECT id FROM pet_info WHERE name = '咪咪' LIMIT 1), '咪咪的到来', '咪咪是一只小橘猫，刚来的时候只有手掌大。现在它已经长成了一个胖嘟嘟的小毛球，每天都给我们带来欢笑。', 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=600&q=80', 1);

-- 验证结果
SELECT '=== 宠物信息 ===' AS info;
SELECT id, name, breed, status FROM pet_info ORDER BY id;

SELECT '=== 故事信息 ===' AS info;
SELECT s.id, s.title, s.pet_id, p.name AS pet_name 
FROM story_info s 
LEFT JOIN pet_info p ON s.pet_id = p.id 
ORDER BY s.id;
