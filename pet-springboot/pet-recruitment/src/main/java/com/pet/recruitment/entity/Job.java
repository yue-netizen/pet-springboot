package com.pet.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recruitment_job")
public class Job extends BaseEntity {

    private String title;
    private String type;
    private String location;
    private String description;
    private String requirement;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Integer status;
}
