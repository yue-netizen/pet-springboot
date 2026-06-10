package com.pet.social.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_topic")
public class Topic extends BaseEntity {

    private String name;
    private Integer useCount = 0;
}
