package com.pet.pet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.constant.RedisConstants;
import com.pet.common.dto.PetDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.pet.entity.Adoption;
import com.pet.pet.entity.Pet;
import com.pet.pet.mapper.AdoptionMapper;
import com.pet.pet.mapper.PetMapper;
import com.pet.pet.service.PetService;
import com.pet.pet.vo.AdoptionApplyVO;
import com.pet.pet.vo.PetQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    private final AdoptionMapper adoptionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<Page<Pet>> getPetList(PetQueryVO queryVO) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(queryVO.getName())) {
            wrapper.like(Pet::getName, queryVO.getName());
        }
        if (StrUtil.isNotBlank(queryVO.getType())) {
            wrapper.eq(Pet::getType, queryVO.getType());
        }
        if (StrUtil.isNotBlank(queryVO.getBreed())) {
            wrapper.like(Pet::getBreed, queryVO.getBreed());
        }
        if (StrUtil.isNotBlank(queryVO.getAge())) {
            wrapper.eq(Pet::getAge, queryVO.getAge());
        }
        if (queryVO.getStatus() != null) {
            wrapper.eq(Pet::getStatus, queryVO.getStatus());
        }
        
        wrapper.orderByDesc(Pet::getCreateTime);
        
        Page<Pet> page = new Page<>(queryVO.getPage(), queryVO.getSize());
        Page<Pet> result = this.page(page, wrapper);
        
        return Result.success(result);
    }

    @Override
    public Result<PetDTO> getPetById(Long id) {
        String cacheKey = RedisConstants.PET_DETAIL_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return Result.success((PetDTO) cached);
        }
        
        Pet pet = this.getById(id);
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }
        
        PetDTO petDTO = BeanUtil.copyProperties(pet, PetDTO.class);
        
        redisTemplate.opsForValue().set(
                cacheKey,
                petDTO,
                RedisConstants.CACHE_EXPIRE_TIME,
                TimeUnit.SECONDS
        );
        
        return Result.success(petDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addPet(Pet pet) {
        pet.setStatus(1);
        this.save(pet);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updatePet(Pet pet) {
        Pet existingPet = this.getById(pet.getId());
        if (existingPet == null) {
            throw BusinessException.of("宠物不存在");
        }
        
        this.updateById(pet);
        
        redisTemplate.delete(RedisConstants.PET_DETAIL_KEY + pet.getId());
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deletePet(Long id) {
        this.removeById(id);
        redisTemplate.delete(RedisConstants.PET_DETAIL_KEY + id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> applyAdoption(AdoptionApplyVO applyVO, Long userId) {
        Pet pet = this.getById(applyVO.getPetId());
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }
        
        if (pet.getStatus() != 1) {
            throw BusinessException.of("该宠物暂不可领养");
        }
        
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Adoption::getUserId, userId)
                .eq(Adoption::getPetId, applyVO.getPetId())
                .in(Adoption::getStatus, 0, 1);
        long count = adoptionMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.of("您已申请过该宠物的领养");
        }
        
        Adoption adoption = new Adoption();
        adoption.setUserId(userId);
        adoption.setPetId(applyVO.getPetId());
        adoption.setStatus(0);
        adoption.setReason(applyVO.getReason());
        adoption.setAddress(applyVO.getAddress());
        adoption.setPhone(applyVO.getPhone());
        
        adoptionMapper.insert(adoption);
        
        return Result.success();
    }

    @Override
    public Result<Page<Pet>> getMyAdoptions(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Adoption> adoptionWrapper = new LambdaQueryWrapper<>();
        adoptionWrapper.eq(Adoption::getUserId, userId)
                .orderByDesc(Adoption::getCreateTime);
        
        Page<Adoption> adoptionPage = new Page<>(page, size);
        adoptionMapper.selectPage(adoptionPage, adoptionWrapper);
        
        Page<Pet> petPage = new Page<>(page, size);
        petPage.setTotal(adoptionPage.getTotal());
        petPage.setRecords(adoptionPage.getRecords().stream()
                .map(adoption -> this.getById(adoption.getPetId()))
                .toList());
        
        return Result.success(petPage);
    }
}
