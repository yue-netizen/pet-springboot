package com.pet.admin.vo;

import lombok.Data;

@Data
public class AdoptionDetailVO {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private String userPhone;
    private Long petId;
    private String petName;
    private String petBreed;
    private String petAge;
    private String petImage;
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
    private String createTime;
}