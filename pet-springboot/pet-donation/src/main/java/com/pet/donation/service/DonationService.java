package com.pet.donation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.donation.entity.Donation;
import com.pet.donation.vo.DonationVO;

import java.math.BigDecimal;

public interface DonationService extends IService<Donation> {

    Result<Page<Donation>> getDonationList(Integer page, Integer size);

    Result<Donation> createDonation(DonationVO donationVO, Long userId);

    Result<BigDecimal> getTotalAmount();

    Result<Page<Donation>> getMyDonations(Long userId, Integer page, Integer size);
}
