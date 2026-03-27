package com.pet.pet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_adoption")
public class Adoption extends BaseEntity {

    private Long userId;
    private Long petId;
    private Integer status;
    private String reason;
    private String address;
    private String phone;
    private String reviewNote;
}
