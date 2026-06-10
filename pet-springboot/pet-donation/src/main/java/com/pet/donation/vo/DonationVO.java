package com.pet.donation.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DonationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "捐赠金额不能为空")
    @DecimalMin(value = "0.01", message = "捐赠金额必须大于0")
    private BigDecimal amount;

    private String paymentMethod;

    private String remark;
}
