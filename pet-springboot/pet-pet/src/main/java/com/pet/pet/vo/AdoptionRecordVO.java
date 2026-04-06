package com.pet.pet.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdoptionRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long petId;
    private Integer status;
    private String reason;
    private String address;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String petName;
    private String petImage;
    private String petBreed;
    private String petType;
}
