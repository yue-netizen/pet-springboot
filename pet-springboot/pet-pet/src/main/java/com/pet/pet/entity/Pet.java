package com.pet.pet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_info")
public class Pet extends BaseEntity {

    private String name;
    private String breed;
    private String age;
    private String type;
    private String gender;
    private String image;
    private String description;
    private String healthStatus;
    private Integer status;
    private Long shelterId;
}
