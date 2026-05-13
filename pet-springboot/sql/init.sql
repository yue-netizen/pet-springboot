-- 宠物领养平台数据库初始化脚本
CREATE DATABASE IF NOT EXISTS pet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    gender VARCHAR(10) DEFAULT NULL COMMENT '性别',
    birthday DATE DEFAULT NULL COMMENT '出生日期',
    region VARCHAR(100) DEFAULT NULL COMMENT '所在地区',
    address VARCHAR(255) COMMENT '地址',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    role INT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 宠物信息表
CREATE TABLE IF NOT EXISTS pet_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '宠物ID',
    name VARCHAR(50) NOT NULL COMMENT '宠物名称',
    breed VARCHAR(50) COMMENT '品种',
    age VARCHAR(20) COMMENT '年龄',
    type VARCHAR(20) COMMENT '类型：dog-狗，cat-猫，bird-鸟，other-其他',
    gender VARCHAR(10) COMMENT '性别：male-公，female-母',
    image VARCHAR(255) COMMENT '图片',
    description TEXT COMMENT '描述',
    health_status VARCHAR(100) COMMENT '健康状态',
    status INT DEFAULT 1 COMMENT '状态：0-不可领养，1-可领养，2-已领养',
    shelter_id BIGINT COMMENT '收容所ID',
    story TEXT COMMENT '宠物故事',
    size VARCHAR(50) COMMENT '体型（小型/中型/大型）',
    personality VARCHAR(200) COMMENT '性格特点',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息表';

-- 领养申请表
CREATE TABLE IF NOT EXISTS pet_adoption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    status INT DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    reason TEXT COMMENT '领养原因',
    address VARCHAR(255) COMMENT '地址',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    applicant_age INT COMMENT '申请人年龄',
    housing_type VARCHAR(100) COMMENT '居住类型',
    has_pet_experience VARCHAR(20) COMMENT '是否有养宠经验',
    family_status TEXT COMMENT '家庭成员情况',
    agree_health_check TINYINT DEFAULT 0 COMMENT '是否同意健康检查',
    agree_neuter TINYINT DEFAULT 0 COMMENT '是否同意绝育',
    agree_good_environment TINYINT DEFAULT 0 COMMENT '是否同意提供良好环境',
    agree_timely_medical TINYINT DEFAULT 0 COMMENT '是否同意及时就医',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- 宠物收藏表
CREATE TABLE IF NOT EXISTS pet_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_pet` (`user_id`, `pet_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pet_id` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物收藏表';

-- 社交帖子表
CREATE TABLE IF NOT EXISTS social_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '内容',
    image VARCHAR(255) COMMENT '图片',
    images TEXT COMMENT '多张图片URL，逗号分隔',
    video VARCHAR(255) COMMENT '视频',
    videos TEXT COMMENT '多个视频URL，逗号分隔',
    tags TEXT COMMENT '标签，逗号分隔',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    status INT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS social_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞表（多态）
CREATE TABLE IF NOT EXISTS social_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    target_type INT NOT NULL COMMENT '目标类型：1-帖子，2-故事',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 话题表
CREATE TABLE IF NOT EXISTS social_topic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '话题名称',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话题表';

-- 故事点赞表
CREATE TABLE IF NOT EXISTS story_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    story_id BIGINT NOT NULL COMMENT '故事ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故事点赞表';

-- 聊天会话表
CREATE TABLE IF NOT EXISTS chat_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user1_id BIGINT NOT NULL COMMENT '用户1ID',
    user2_id BIGINT NOT NULL COMMENT '用户2ID',
    last_message VARCHAR(1000) COMMENT '最后一条消息',
    last_message_time DATETIME DEFAULT NULL COMMENT '最后消息时间',
    unread_count1 INT DEFAULT 0 COMMENT '用户1未读数',
    unread_count2 INT DEFAULT 0 COMMENT '用户2未读数',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content LONGTEXT NOT NULL COMMENT '消息内容',
    type INT DEFAULT 1 COMMENT '消息类型：1-文本，2-图片',
    status INT DEFAULT 0 COMMENT '状态：0-未读，1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 捐赠记录表
CREATE TABLE IF NOT EXISTS donation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '捐赠ID',
    user_id BIGINT COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '捐赠金额',
    payment_method VARCHAR(20) DEFAULT 'alipay' COMMENT '支付方式：alipay-支付宝，wechat-微信',
    status INT DEFAULT 1 COMMENT '状态：0-待支付，1-已支付',
    transaction_id VARCHAR(100) COMMENT '交易号',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='捐赠记录表';

-- 招聘职位表
CREATE TABLE IF NOT EXISTS recruitment_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
    title VARCHAR(100) NOT NULL COMMENT '职位名称',
    type VARCHAR(50) COMMENT '工作类型：full-time-全职，part-time-兼职，remote-远程',
    location VARCHAR(100) COMMENT '工作地点',
    description TEXT COMMENT '职位描述',
    requirement TEXT COMMENT '任职要求',
    salary_min DECIMAL(10,2) COMMENT '最低薪资',
    salary_max DECIMAL(10,2) COMMENT '最高薪资',
    status INT DEFAULT 1 COMMENT '状态：0-关闭，1-开放',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘职位表';

-- 招聘申请表
CREATE TABLE IF NOT EXISTS recruitment_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    age INT COMMENT '年龄',
    address VARCHAR(255) COMMENT '居住地址',
    resume TEXT COMMENT '简历',
    introduction TEXT COMMENT '自我介绍',
    availability VARCHAR(100) COMMENT '可工作时间',
    status INT DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    review_note VARCHAR(255) COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘申请表';

-- 员工表（已通过的求职人员）
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    job_id BIGINT NOT NULL COMMENT '申请的岗位ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    age INT COMMENT '年龄',
    address VARCHAR(255) COMMENT '居住地址',
    resume TEXT COMMENT '简历/经验',
    introduction TEXT COMMENT '自我介绍/申请理由',
    availability VARCHAR(100) COMMENT '可工作时间',
    position VARCHAR(50) COMMENT '职位名称',
    department VARCHAR(50) COMMENT '部门',
    hire_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入职日期',
    status INT DEFAULT 1 COMMENT '状态：0-离职，1-在职，2-休假',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 领养故事表
CREATE TABLE IF NOT EXISTS story_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '故事ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT COMMENT '宠物ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    image VARCHAR(255) COMMENT '图片',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    status INT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养故事表';

-- 故事评论表
CREATE TABLE IF NOT EXISTS story_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    story_id BIGINT NOT NULL COMMENT '故事ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故事评论表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关注者用户ID',
    target_user_id BIGINT NOT NULL COMMENT '被关注用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_follow (user_id, target_user_id),
    INDEX idx_target_user (target_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 养宠贴士表
CREATE TABLE IF NOT EXISTS pet_tips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '贴士标题',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    content TEXT NOT NULL COMMENT '贴士内容',
    cover_image VARCHAR(500) COMMENT '封面图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养宠贴士表';

-- AI聊天历史表
CREATE TABLE IF NOT EXISTS ai_chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：user/assistant',
    content TEXT NOT NULL COMMENT '内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天历史表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_name VARCHAR(100) COMMENT '配置名称',
    description VARCHAR(255) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO sys_config (config_key, config_value, config_name, description) VALUES
('about_intro', '爪印之家是一家致力于为流浪、被遗弃和被送养的动物提供安全庇护所的非营利宠物福利组织。我们的使命建立在每一个动物都值得拥有一个充满爱的永久家园的信念之上。', '关于我们-简介', '关于我们页面简介内容'),
('about_intro_image', 'https://images.unsplash.com/photo-1528301721190-1c922d627ea4?auto=format&fit=crop&w=1200&q=80', '关于我们-简介图片', '关于我们页面简介图片'),
('about_status', '目前，我们正在满负荷运营，每天持续接收新动物，同时严格审核领养申请。', '关于我们-当前状况', '关于我们页面当前状况内容'),
('about_status_stat1', '5,000+', '当前状况-统计1数值', '成功领养数量'),
('about_status_stat1_label', '成功领养', '当前状况-统计1标签', '成功领养标签'),
('about_status_stat2', '12', '当前状况-统计2数值', '收容所数量'),
('about_status_stat2_label', '收容所', '当前状况-统计2标签', '收容所标签'),
('about_status_stat3', '200+', '当前状况-统计3数值', '志愿者数量'),
('about_status_stat3_label', '志愿者', '当前状况-统计3标签', '志愿者标签'),
('about_history', '2018年：爪印之家正式成立，最初只有一个小型收容所。\n2019年：扩展到3个收容所，成功帮助500只动物找到新家。\n2020年：建立志愿者网络，志愿者人数突破100人。\n2021年：开设宠物医疗中心，提供专业医疗服务。\n2022年：成功领养数量突破3000只。\n2023年：成为本地区最大的宠物福利组织。', '关于我们-发展历程', '关于我们页面发展历程内容'),
('about_news', '2024年春季领养活动即将开启\n我们将于4月举办大型领养活动，届时将有超过100只可爱的小动物等待新家。\n\n新医疗中心正式运营\n我们的第二医疗中心已于3月正式投入使用，为更多宠物提供医疗服务。\n\n志愿者招募进行中\n我们正在招募更多爱心志愿者，欢迎加入我们的大家庭。', '关于我们-最新动态', '关于我们页面最新动态内容'),
('about_contact', '地址：北京市朝阳区宠物街123号\n电话：400-123-4567\n邮箱：contact@pawhome.com\n工作时间：周一至周日 9:00-18:00', '关于我们-联系我们', '关于我们页面联系我们内容'),
('about_contact_image', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=1200&q=80', '关于我们-联系我们图片', '关于我们页面联系我们图片'),
('adoption_rules', '申请人必须年满18周岁，并持有有效的政府签发身份证件。\n必须核实安全稳定的家庭环境（可能需要家庭视频参观或实地访问）。\n家庭中的所有成员必须同意领养。\n如果是租房，必须提供房东关于宠物饲养的书面批准。\n申请人必须具备提供常规兽医护理、紧急医疗支持和优质营养的经济能力。\n从爪印之家领养的宠物不得出售、交易、遗弃或用于商业繁殖。\n领养人必须同意在前6个月内通过我们的沟通平台进行动态跟进联系。', '领养规则', '领养规则内容'),
('adoption_agreement', '本协议由以下双方于[日期]签订：\n甲方：爪印之家\n乙方：领养人\n\n1. 甲方将以下宠物转让给乙方：\n宠物名称：\n品种：\n年龄：\n性别：\n\n2. 乙方同意：\n- 为宠物提供安全、舒适的生活环境\n- 按时为宠物接种疫苗并进行定期体检\n- 不得虐待或忽视宠物\n- 如遇特殊情况无法继续饲养，应及时通知甲方并协商解决方案\n\n3. 本协议自双方签字之日起生效。', '领养协议', '领养协议内容'),
('foster_rules', '寄养申请人必须年满18周岁，并能提供有效身份证件。\n寄养家庭必须有足够的空间和时间照顾寄养的宠物。\n必须确保寄养环境安全，无潜在危险物品。\n寄养期间需保持与爪印之家的日常沟通，及时反馈宠物状态。\n如需带寄养宠物外出，需提前告知爪印之家并获得同意。\n不得将寄养宠物转交给第三方照顾，除非事先获得书面批准。\n寄养期间如宠物生病或受伤，需第一时间通知爪印之家并按照指示处理。', '寄养规则', '寄养规则内容'),
('foster_agreement', '寄养协议\n\n甲方：爪印之家\n乙方：寄养人\n\n1. 甲方委托乙方在[开始日期]至[结束日期]期间照顾以下宠物：\n宠物名称：\n品种：\n年龄：\n性别：\n特殊需求：\n\n2. 乙方责任：\n- 提供符合宠物需求的饮食和居住环境\n- 每日陪伴和适当运动\n- 按照要求进行日常护理\n- 及时向甲方报告宠物的健康和行为状况\n- 紧急情况下联系甲方指定的兽医\n\n3. 甲方责任：\n- 提供宠物的食品、用品和必要的医疗费用\n- 提供详细的照顾指南\n- 保持定期沟通\n\n4. 本协议自双方签字之日起生效。', '寄养协议', '寄养协议内容');

-- 插入测试数据
INSERT INTO sys_user (username, password, nickname, email, phone, status, role) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@pet.com', '13800000000', 1, 1);

INSERT INTO sys_user (username, password, nickname, email, phone, status, role) VALUES
('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'test@pet.com', '13800000001', 1, 0);

INSERT INTO pet_info (name, breed, age, type, gender, image, description, health_status, status, story, size, personality) VALUES
('小黄', '金毛寻回犬', '2岁', '狗', '公', 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80', '性格温顺，活泼可爱，喜欢和人玩耍', '健康', 1, '《纸箱里的小火焰》清晨四点的垃圾堆旁，志愿者小陈听见微弱的呜咽。翻动纸箱时，一团姜黄色的毛球滚进他怀里，那是只不足巴掌大的柴犬幼崽，肚皮上还沾着干草。现在这只被取名"栗子"的小狗在救助站已经学会撒欢奔跑。', '大型', '活泼'),
('露娜', '波斯猫', '6个月', '猫', '母', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80', '温柔优雅，喜欢安静的环境', '健康', 1, '《窗边的小公主》露娜是在一个雨夜被发现的，它蜷缩在公寓楼下的纸箱里，浑身湿透。现在的露娜已经是救助站的小公主了，每天最爱趴在窗台上看外面的风景。', '小型', '安静'),
('查理', '比格混血', '3岁', '狗', '公', 'https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=600&q=80', '聪明伶俐，嗅觉灵敏，是个小侦探', '健康', 1, '《小侦探查理》查理以前是一只流浪狗，在街头流浪了很久。它特别聪明，总能找到藏起来的食物。现在在救助站，它成了大家的开心果。', '中型', '聪明'),
('米洛', '缅因猫', '4岁', '猫', '公', 'https://images.unsplash.com/photo-1513245543132-31f507417b26?auto=format&fit=crop&w=600&q=80', '体型巨大，性格温柔，是个温柔的巨人', '健康', 1, '《温柔的巨人》米洛是被主人遗弃的，因为主人要搬家不能带它走。它虽然体型很大，但性格特别温柔，喜欢被人抚摸。', '大型', '温柔'),
('库珀', '柯基犬', '1岁', '狗', '公', 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=600&q=80', '短腿可爱，精力充沛，喜欢跑步', '健康', 1, '《短腿小勇士》库珀是在宠物市场被救助的，当时它被关在狭小的笼子里。现在的库珀每天都在救助站的院子里快乐地奔跑，它的短腿跑得特别快！', '小型', '活泼'),
('黛西', '虎斑猫', '2个月', '猫', '母', 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=600&q=80', '小猫一只，正在学习探索世界', '健康', 1, '《小探险家黛西》黛西是一只刚出生不久的小猫，被发现时它和妈妈走散了。现在在救助站，它每天都在探索新事物，对什么都充满好奇。', '小型', '好奇');

INSERT INTO recruitment_job (title, type, location, description, requirement, status) VALUES
('兽医', 'full-time', '城市收容所总部', '负责宠物日常健康检查和治疗工作', '持有兽医执业资格证，有相关工作经验优先', 1),
('领养协调员', 'full-time', '市中心分部', '负责领养申请审核和协调工作', '热爱动物，有良好的沟通能力', 1),
('动物护理志愿者', 'part-time', '所有地点', '协助照顾日常宠物护理工作', '热爱动物，有爱心和耐心', 1),
('社交媒体运营', 'remote', '远程办公', '负责社交媒体账号运营和内容创作', '熟悉各大社交平台，有运营经验', 1);

INSERT INTO story_info (user_id, pet_id, title, content, image, status) VALUES
(1, 1, '洛奇的新开始', '我们发现洛奇时，它只是一只胆小的小狗。爪印之家的团队确保它完全康复。把它带回家给我们带来了巨大的快乐。它不仅仅是宠物，它是我们的家人。', 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80', 1);

INSERT INTO pet_tips (title, category, content, create_time, update_time, deleted) VALUES
('幼犬营养指南', '饮食', '<h2>幼犬营养指南</h2><p>幼犬正处于快速生长发育阶段，需要均衡的营养来支持健康成长。</p><h3>1. 蛋白质需求</h3><p>幼犬需要高质量的蛋白质来支持肌肉和组织生长。</p><h3>2. 适量脂肪</h3><p>健康的脂肪能提供幼犬所需的能量。</p><h3>3. 定时定量喂食</h3><p>建议每天3-4次定时定量喂食。</p>', NOW(), NOW(), 0),
('如何与救助宠物建立信任', '行为', '<h2>如何与救助宠物建立信任</h2><p>救助宠物往往经历过流浪或被遗弃的创伤，需要更多耐心和关爱。</p><h3>1. 给它们空间</h3><p>刚到家的救助宠物需要时间适应新环境。</p><h3>2. 建立日常规律</h3><p>固定的喂食时间能让宠物感到安全。</p>', NOW(), NOW(), 0),
('家庭美容要点', '健康', '<h2>家庭美容要点</h2><p>定期美容不仅能让宠物看起来整洁，还能促进皮肤健康。</p><h3>1. 梳毛频率</h3><p>根据宠物毛发类型确定梳毛频率。</p><h3>2. 洗澡技巧</h3><p>使用宠物专用沐浴露，水温适中。</p>', NOW(), NOW(), 0);
