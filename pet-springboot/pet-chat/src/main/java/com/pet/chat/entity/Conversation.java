package com.pet.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_conversation")
public class Conversation extends BaseEntity {

    private Long user1Id;
    private Long user2Id;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount1;
    private Integer unreadCount2;
    private Integer status;

    @TableField(exist = false)
    private String user1Nickname;

    @TableField(exist = false)
    private String user1Avatar;

    @TableField(exist = false)
    private String user2Nickname;

    @TableField(exist = false)
    private String user2Avatar;
}
