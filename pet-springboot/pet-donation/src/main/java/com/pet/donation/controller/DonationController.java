package com.pet.donation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.donation.entity.Donation;
import com.pet.donation.service.DonationService;
import com.pet.donation.vo.DonationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "捐赠管理")
@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @Operation(summary = "获取捐赠列表")
    @GetMapping("/list")
    public Result<Page<Donation>> getDonationList(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return donationService.getDonationList(page, size);
    }

    @Operation(summary = "创建捐赠")
    @PostMapping
    public Result<Donation> createDonation(@Valid @RequestBody DonationVO donationVO,
                                            @RequestHeader("X-User-Id") Long userId) {
        return donationService.createDonation(donationVO, userId);
    }

    @Operation(summary = "获取捐赠总额")
    @GetMapping("/total")
    public Result<BigDecimal> getTotalAmount() {
        return donationService.getTotalAmount();
    }

    @Operation(summary = "获取我的捐赠记录")
    @GetMapping("/my")
    public Result<Page<Donation>> getMyDonations(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestHeader("X-User-Id") Long userId) {
        return donationService.getMyDonations(userId, page, size);
    }
}
