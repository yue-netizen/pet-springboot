package com.pet.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recruitment_application")
public class JobApplication extends BaseEntity {

    private Long jobId;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private Integer age;
    private String address;
    private String resume;
    private String introduction;
    private String availability;
    private Integer status;
    private String reviewNote;
}
