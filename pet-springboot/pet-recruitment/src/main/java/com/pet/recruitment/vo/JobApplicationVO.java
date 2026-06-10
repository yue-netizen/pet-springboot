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

    @NotNull(message = "年龄不能为空")
    private Integer age;

    @NotBlank(message = "居住地址不能为空")
    private String address;

    @NotBlank(message = "相关经验不能为空")
    private String resume;

    @NotBlank(message = "申请理由不能为空")
    private String introduction;

    @NotBlank(message = "可工作时间不能为空")
    private String availability;
}
