package com.pet.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("donation_record")
public class Donation {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private Integer status;
    private String transactionId;
    private String remark;
    private LocalDateTime createTime;
}
