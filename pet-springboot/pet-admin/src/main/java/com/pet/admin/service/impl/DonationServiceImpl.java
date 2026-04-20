package com.pet.admin.service.impl;

import com.pet.admin.mapper.DonationMapper;
import com.pet.admin.service.DonationService;
import com.pet.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationMapper donationMapper;

    @Override
    public Result<List<Map<String, Object>>> getDonationList() {
        return Result.success(donationMapper.getDonationListWithUser());
    }

    @Override
    public Result<List<Map<String, Object>>> getDonationStatsByUser() {
        return Result.success(donationMapper.getDonationStatsByUser());
    }

    @Override
    public Result<BigDecimal> getTotalAmount() {
        return Result.success(donationMapper.getTotalAmount());
    }
}
