package com.pet.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Adoption;
import com.pet.admin.entity.Pet;
import com.pet.admin.entity.User;
import com.pet.admin.mapper.AdoptionMapper;
import com.pet.admin.mapper.PetMapper;
import com.pet.admin.mapper.UserMapper;
import com.pet.admin.service.AdoptionService;
import com.pet.admin.vo.AdoptionDetailVO;
import com.pet.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl extends ServiceImpl<AdoptionMapper, Adoption> implements AdoptionService {

    private final UserMapper userMapper;
    private final PetMapper petMapper;

    @Override
    public Result<Page<AdoptionDetailVO>> getAdoptionList(Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Adoption::getStatus, status);
        }
        wrapper.orderByDesc(Adoption::getCreateTime);

        Page<Adoption> adoptionPage = this.page(new Page<>(page, size), wrapper);

        List<Adoption> adoptions = adoptionPage.getRecords();
        List<Long> userIds = adoptions.stream().map(Adoption::getUserId).distinct().toList();
        List<Long> petIds = adoptions.stream().map(Adoption::getPetId).distinct().toList();

        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() : 
            userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        
        Map<Long, Pet> petMap = petIds.isEmpty() ? Map.of() : 
            petMapper.selectBatchIds(petIds).stream().collect(Collectors.toMap(Pet::getId, p -> p));

        List<AdoptionDetailVO> vos = adoptions.stream().map(adoption -> {
            AdoptionDetailVO vo = new AdoptionDetailVO();
            BeanUtil.copyProperties(adoption, vo);
            User user = userMap.get(adoption.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setUserEmail(user.getEmail());
                vo.setUserPhone(user.getPhone());
            }
            Pet pet = petMap.get(adoption.getPetId());
            if (pet != null) {
                vo.setPetName(pet.getName());
                vo.setPetBreed(pet.getBreed());
                vo.setPetAge(pet.getAge());
                vo.setPetImage(pet.getImage());
            }
            return vo;
        }).toList();

        Page<AdoptionDetailVO> resultPage = new Page<>(page, size, adoptionPage.getTotal());
        resultPage.setRecords(vos);

        return Result.success(resultPage);
    }

    @Override
    public Result<AdoptionDetailVO> getAdoptionDetail(Long id) {
        Adoption adoption = this.getById(id);
        if (adoption == null) {
            return Result.error("领养申请不存在");
        }

        AdoptionDetailVO vo = new AdoptionDetailVO();
        BeanUtil.copyProperties(adoption, vo);

        User user = userMapper.selectById(adoption.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setUserEmail(user.getEmail());
            vo.setUserPhone(user.getPhone());
        }

        Pet pet = petMapper.selectById(adoption.getPetId());
        if (pet != null) {
            vo.setPetName(pet.getName());
            vo.setPetBreed(pet.getBreed());
            vo.setPetAge(pet.getAge());
            vo.setPetImage(pet.getImage());
        }

        return Result.success(vo);
    }

    @Override
    public Result<Void> reviewAdoption(Long id, Integer status) {
        Adoption adoption = this.getById(id);
        if (adoption == null) {
            return Result.error("领养申请不存在");
        }
        if (adoption.getStatus() != 0) {
            return Result.error("该申请已审核过了");
        }

        adoption.setStatus(status);
        this.updateById(adoption);

        if (status == 1) {
            Pet pet = petMapper.selectById(adoption.getPetId());
            if (pet != null) {
                pet.setStatus(2);
                petMapper.updateById(pet);
            }
        }

        return Result.success();
    }

    @Override
    public Result<Void> updateAdoption(Adoption adoption) {
        this.updateById(adoption);
        return Result.success();
    }
}