package com.pet.recruitment.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class JobApplicationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "职位ID不能为空")
    private Long jobId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    private String resume;

    private String introduction;
}
