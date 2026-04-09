package com.pet.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_follow")
public class Follow {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetUserId;
    private LocalDateTime createTime;
}
