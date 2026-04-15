package com.pet.chat.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_chat_history")
public class AiChatHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionId;

    private Long userId;

    private String role;

    private String content;

    private LocalDateTime createdAt;
}
