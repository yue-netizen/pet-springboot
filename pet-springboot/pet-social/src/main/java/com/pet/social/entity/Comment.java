package com.pet.social.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_comment")
public class Comment extends BaseEntity {

    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Integer status;

    @TableField(exist = false)
    private String userNickname;

    @TableField(exist = false)
    private String userAvatar;
}
