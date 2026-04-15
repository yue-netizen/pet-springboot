package com.pet.chat.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet_adoption")
public class Adoption {

    @TableId(type = IdType.AUTO)
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
}
