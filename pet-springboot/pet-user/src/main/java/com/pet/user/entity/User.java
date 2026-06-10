package com.pet.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String gender;
    private LocalDate birthday;
    private String region;
    private String address;
    private Integer status;
    private Integer role;
}
