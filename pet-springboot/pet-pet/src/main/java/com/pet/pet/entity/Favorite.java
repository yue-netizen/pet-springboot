package com.pet.pet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_favorite")
public class Favorite extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long petId;
}
