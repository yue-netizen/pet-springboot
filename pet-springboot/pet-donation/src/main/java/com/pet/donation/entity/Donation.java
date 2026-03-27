package com.pet.donation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("donation_record")
public class Donation extends BaseEntity {

    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private Integer status;
    private String transactionId;
    private String remark;
}
