package com.pet.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recruitment_job")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String type;

    private String location;

    private String description;

    private String requirement;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
