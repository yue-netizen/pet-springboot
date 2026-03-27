package com.pet.story.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("story_info")
public class Story extends BaseEntity {

    private Long userId;
    private Long petId;
    private String title;
    private String content;
    private String image;
    private Integer likeCount;
    private Integer viewCount;
    private Integer status;
}
