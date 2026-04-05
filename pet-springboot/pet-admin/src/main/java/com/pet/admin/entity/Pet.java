package com.pet.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("pet_info")
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

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
    private String story;
    private String size;
    private String personality;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
