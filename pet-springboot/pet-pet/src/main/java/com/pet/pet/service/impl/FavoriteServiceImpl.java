package com.pet.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.dto.PetDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.pet.entity.Favorite;
import com.pet.pet.entity.Pet;
import com.pet.pet.mapper.FavoriteMapper;
import com.pet.pet.mapper.PetMapper;
import com.pet.pet.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final PetMapper petMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addFavorite(Long petId, Long userId) {
        Pet pet = petMapper.selectById(petId);
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId);
        if (this.count(wrapper) > 0) {
            throw BusinessException.of("已经收藏过了");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        this.save(favorite);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> removeFavorite(Long petId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId);
        this.remove(wrapper);
        return Result.success();
    }

    @Override
    public Result<Boolean> checkFavorite(Long petId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getPetId, petId);
        boolean exists = this.count(wrapper) > 0;
        return Result.success(exists);
    }

    @Override
    public Result<Page<PetDTO>> getMyFavorites(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);

        Page<Favorite> favoritePage = new Page<>(page, size);
        this.page(favoritePage, wrapper);

        List<Long> petIds = favoritePage.getRecords().stream()
                .map(Favorite::getPetId)
                .collect(Collectors.toList());

        if (petIds.isEmpty()) {
            return Result.success(new Page<>());
        }

        List<Pet> pets = petMapper.selectBatchIds(petIds);

        List<PetDTO> petDTOList = pets.stream().map(pet -> {
            PetDTO dto = new PetDTO();
            dto.setId(pet.getId());
            dto.setName(pet.getName());
            dto.setBreed(pet.getBreed());
            dto.setAge(pet.getAge());
            dto.setGender(pet.getGender());
            dto.setSize(pet.getSize());
            dto.setImage(pet.getImage());
            dto.setStatus(pet.getStatus());
            return dto;
        }).collect(Collectors.toList());

        Page<PetDTO> result = new Page<>(page, size);
        result.setTotal(favoritePage.getTotal());
        result.setRecords(petDTOList);

        return Result.success(result);
    }
}
