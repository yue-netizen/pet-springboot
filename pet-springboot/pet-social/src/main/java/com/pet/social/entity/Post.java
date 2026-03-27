package com.pet.social.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_post")
public class Post extends BaseEntity {

    private Long userId;
    private String content;
    private String image;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
}
