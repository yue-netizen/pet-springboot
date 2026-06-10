package com.pet.pet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import com.pet.pet.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {

    Result<Void> addFavorite(Long petId, Long userId);

    Result<Void> removeFavorite(Long petId, Long userId);

    Result<Boolean> checkFavorite(Long petId, Long userId);

    Result<Page<PetDTO>> getMyFavorites(Long userId, Integer page, Integer size);
}
