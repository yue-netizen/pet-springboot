package com.pet.donation.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.result.Result;
import com.pet.donation.entity.Donation;
import com.pet.donation.mapper.DonationMapper;
import com.pet.donation.service.DonationService;
import com.pet.donation.vo.DonationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl extends ServiceImpl<DonationMapper, Donation> implements DonationService {

    @Override
    public Result<Page<Donation>> getDonationList(Integer page, Integer size) {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getStatus, 1)
                .orderByDesc(Donation::getCreateTime);
        
        Page<Donation> donationPage = new Page<>(page, size);
        Page<Donation> result = this.page(donationPage, wrapper);
        
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Donation> createDonation(DonationVO donationVO, Long userId) {
        Donation donation = new Donation();
        donation.setUserId(userId);
        donation.setAmount(donationVO.getAmount());
        donation.setPaymentMethod(donationVO.getPaymentMethod() != null ? donationVO.getPaymentMethod() : "alipay");
        donation.setStatus(1);
        donation.setTransactionId(IdUtil.fastSimpleUUID());
        donation.setRemark(donationVO.getRemark());
        
        this.save(donation);
        
        return Result.success(donation);
    }

    @Override
    public Result<BigDecimal> getTotalAmount() {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getStatus, 1);
        
        List<Donation> donations = this.list(wrapper);
        BigDecimal total = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return Result.success(total);
    }

    @Override
    public Result<Page<Donation>> getMyDonations(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Donation::getUserId, userId)
                .orderByDesc(Donation::getCreateTime);
        
        Page<Donation> donationPage = new Page<>(page, size);
        Page<Donation> result = this.page(donationPage, wrapper);
        
        return Result.success(result);
    }
}
