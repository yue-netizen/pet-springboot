package com.pet.tips.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pet.common.result.Result;
import com.pet.tips.entity.PetTips;
import com.pet.tips.service.PetTipsService;
import com.pet.tips.vo.PetTipsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "养宠贴士管理")
@RestController
@RequestMapping("/tips")
@RequiredArgsConstructor
public class PetTipsController {

    private final PetTipsService petTipsService;

    @Operation(summary = "获取贴士列表")
    @GetMapping("/list")
    public Result<IPage<PetTips>> getTipsList(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) String category) {
        return petTipsService.getTipsList(page, size, category);
    }

    @Operation(summary = "获取贴士详情")
    @GetMapping("/{id}")
    public Result<PetTips> getTipsById(@PathVariable Long id) {
        return petTipsService.getTipsById(id);
    }

    @Operation(summary = "创建贴士")
    @PostMapping
    public Result<Void> createTips(@Valid @RequestBody PetTipsVO tipsVO) {
        return petTipsService.createTips(tipsVO);
    }

    @Operation(summary = "更新贴士")
    @PutMapping("/{id}")
    public Result<Void> updateTips(@PathVariable Long id,
                                     @Valid @RequestBody PetTipsVO tipsVO) {
        return petTipsService.updateTips(id, tipsVO);
    }

    @Operation(summary = "删除贴士")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTips(@PathVariable Long id) {
        return petTipsService.deleteTips(id);
    }
}
