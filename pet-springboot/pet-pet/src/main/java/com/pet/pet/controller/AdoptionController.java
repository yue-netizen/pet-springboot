package com.pet.pet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.pet.entity.Pet;
import com.pet.pet.service.PetService;
import com.pet.pet.vo.AdoptionApplyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "领养管理")
@RestController
@RequestMapping("/adoption")
@RequiredArgsConstructor
public class AdoptionController {

    private final PetService petService;

    @Operation(summary = "申请领养")
    @PostMapping("/apply")
    public Result<Void> applyAdoption(@Valid @RequestBody AdoptionApplyVO applyVO,
                                       @RequestHeader("X-User-Id") Long userId) {
        return petService.applyAdoption(applyVO, userId);
    }

    @Operation(summary = "获取我的领养列表")
    @GetMapping("/my")
    public Result<Page<Pet>> getMyAdoptions(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestHeader("X-User-Id") Long userId) {
        return petService.getMyAdoptions(userId, page, size);
    }
}
