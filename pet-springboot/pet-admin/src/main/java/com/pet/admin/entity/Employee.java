package com.pet.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long jobId;

    private String name;

    private String phone;

    private String email;

    private Integer age;

    private String address;

    private String resume;

    private String introduction;

    private String availability;

    private String position;

    private String department;

    private LocalDateTime hireDate;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
