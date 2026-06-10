package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Pet;
import com.pet.admin.mapper.PetMapper;
import com.pet.admin.service.PetService;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Override
    public Result<Page<Pet>> getPetList(Integer page, Integer size) {
        Page<Pet> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Pet::getCreateTime);
        Page<Pet> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Pet> getPetById(Long id) {
        Pet pet = this.getById(id);
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }
        return Result.success(pet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addPet(Pet pet) {
        if (pet.getStatus() == null) {
            pet.setStatus(1);
        }
        this.save(pet);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updatePet(Pet pet) {
        this.updateById(pet);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deletePet(Long id) {
        this.removeById(id);
        return Result.success();
    }
}
