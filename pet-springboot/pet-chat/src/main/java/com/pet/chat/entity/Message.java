package com.pet.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_message")
public class Message extends BaseEntity {

    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer type;
    private Integer status;
    private LocalDateTime readTime;
}
