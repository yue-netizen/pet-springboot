package com.pet.tips.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_tips")
public class PetTips extends BaseEntity {

    private String title;
    private String category;
    private String content;
    private String coverImage;
}
