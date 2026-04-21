ALTER TABLE chat_conversation ADD COLUMN last_message_time DATETIME DEFAULT NULL COMMENT '最后消息时间' AFTER last_message;
