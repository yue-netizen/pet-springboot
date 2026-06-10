package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Adoption;
import com.pet.admin.service.AdoptionService;
import com.pet.admin.vo.AdoptionDetailVO;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "领养申请管理")
@RestController
@RequestMapping("/admin/adoption")
@RequiredArgsConstructor
public class AdminAdoptionController {

    private final AdoptionService adoptionService;

    @Operation(summary = "获取领养申请列表")
    @GetMapping("/list")
    public Result<Page<AdoptionDetailVO>> getAdoptionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return adoptionService.getAdoptionList(page, size, status);
    }

    @Operation(summary = "获取领养申请详情")
    @GetMapping("/{id}")
    public Result<AdoptionDetailVO> getAdoptionDetail(@PathVariable Long id) {
        return adoptionService.getAdoptionDetail(id);
    }

    @Operation(summary = "审核领养申请")
    @PutMapping("/review/{id}")
    public Result<Void> reviewAdoption(@PathVariable Long id, @RequestParam Integer status) {
        return adoptionService.reviewAdoption(id, status);
    }

    @Operation(summary = "编辑领养申请")
    @PutMapping
    public Result<Void> updateAdoption(@RequestBody Adoption adoption) {
        return adoptionService.updateAdoption(adoption);
    }
}