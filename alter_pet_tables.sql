ALTER TABLE pet_info 
ADD COLUMN view_count INT DEFAULT 0 COMMENT '浏览次数' AFTER shelter_id,
ADD COLUMN story TEXT COMMENT '宠物故事' AFTER view_count,
ADD COLUMN size VARCHAR(50) COMMENT '体型（小型/中型/大型）' AFTER story,
ADD COLUMN personality VARCHAR(200) COMMENT '性格特点' AFTER size;

ALTER TABLE pet_adoption
ADD COLUMN email VARCHAR(100) COMMENT '邮箱' AFTER phone,
ADD COLUMN applicant_age INT COMMENT '申请人年龄' AFTER email,
ADD COLUMN housing_type VARCHAR(100) COMMENT '居住类型' AFTER applicant_age,
ADD COLUMN has_pet_experience VARCHAR(20) COMMENT '是否有养宠经验' AFTER housing_type,
ADD COLUMN family_status TEXT COMMENT '家庭成员情况' AFTER has_pet_experience,
ADD COLUMN agree_health_check TINYINT DEFAULT 0 COMMENT '是否同意健康检查' AFTER family_status,
ADD COLUMN agree_neuter TINYINT DEFAULT 0 COMMENT '是否同意绝育' AFTER agree_health_check,
ADD COLUMN agree_good_environment TINYINT DEFAULT 0 COMMENT '是否同意提供良好环境' AFTER agree_neuter,
ADD COLUMN agree_timely_medical TINYINT DEFAULT 0 COMMENT '是否同意及时就医' AFTER agree_good_environment;
