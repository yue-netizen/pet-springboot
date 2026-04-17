-- 员工表（用于存储已通过的求职人员）
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
