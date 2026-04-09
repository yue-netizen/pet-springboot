ALTER TABLE sys_user
    ADD COLUMN gender VARCHAR(10) DEFAULT NULL COMMENT '性别：male-男，female-女' AFTER phone,
    ADD COLUMN birthday DATE DEFAULT NULL COMMENT '出生日期' AFTER gender,
    ADD COLUMN region VARCHAR(100) DEFAULT NULL COMMENT '所在地区' AFTER birthday;
