package com.pet.admin.controller;

import com.pet.admin.service.DonationService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "管理员捐赠管理")
@RestController
@RequestMapping("/admin/donation")
@RequiredArgsConstructor
public class AdminDonationController {

    private final DonationService donationService;

    @Operation(summary = "获取所有捐赠记录（含用户信息）")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getDonationList() {
        return donationService.getDonationList();
    }

    @Operation(summary = "按用户统计捐赠金额")
    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getDonationStats() {
        return donationService.getDonationStatsByUser();
    }

    @Operation(summary = "获取捐赠总额")
    @GetMapping("/total")
    public Result<BigDecimal> getTotalAmount() {
        return donationService.getTotalAmount();
    }
}
