package com.pet.social.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_like")
public class Like extends BaseEntity {

    private Long userId;
    private Long targetId;
    private Integer targetType;
}
