package com.pet.pet.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdoptionApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long petId;

    @NotBlank(message = "领养原因不能为空")
    private String reason;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String email;

    private Integer applicantAge;

    private String housingType;

    private String hasPetExperience;

    private String familyStatus;

    private Boolean agreeHealthCheck;

    private Boolean agreeNeuter;

    private Boolean agreeGoodEnvironment;

    private Boolean agreeTimelyMedical;
}
