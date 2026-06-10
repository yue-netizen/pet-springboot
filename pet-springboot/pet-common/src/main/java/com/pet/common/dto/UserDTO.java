package com.pet.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
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
