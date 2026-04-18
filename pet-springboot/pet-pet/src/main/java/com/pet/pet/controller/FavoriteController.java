package com.pet.pet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import com.pet.pet.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物收藏")
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "收藏宠物")
    @PostMapping("/{petId}")
    public Result<Void> addFavorite(@PathVariable Long petId,
                                     @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.addFavorite(petId, userId);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{petId}")
    public Result<Void> removeFavorite(@PathVariable Long petId,
                                        @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.removeFavorite(petId, userId);
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{petId}")
    public Result<Boolean> checkFavorite(@PathVariable Long petId,
                                         @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.checkFavorite(petId, userId);
    }

    @Operation(summary = "获取我的收藏列表")
    @GetMapping("/my")
    public Result<Page<PetDTO>> getMyFavorites(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestHeader("X-User-Id") Long userId) {
        return favoriteService.getMyFavorites(userId, page, size);
    }
}
