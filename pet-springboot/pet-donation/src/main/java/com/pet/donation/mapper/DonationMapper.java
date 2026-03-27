package com.pet.donation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.donation.entity.Donation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonationMapper extends BaseMapper<Donation> {
}
