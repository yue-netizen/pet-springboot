package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Pet;
import com.pet.admin.service.PetService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员宠物管理", description = "管理员宠物管理接口")
@RestController
@RequestMapping("/admin/pet")
public class AdminPetController {

    @Resource
    private PetService petService;

    @Operation(summary = "获取宠物列表")
    @GetMapping("/list")
    public Result<Page<Pet>> getPetList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return petService.getPetList(page, size);
    }

    @Operation(summary = "获取宠物详情")
    @GetMapping("/{id}")
    public Result<Pet> getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @Operation(summary = "添加宠物")
    @PostMapping
    public Result<Void> addPet(@RequestBody Pet pet) {
        return petService.addPet(pet);
    }

    @Operation(summary = "更新宠物")
    @PutMapping
    public Result<Void> updatePet(@RequestBody Pet pet) {
        return petService.updatePet(pet);
    }

    @Operation(summary = "删除宠物")
    @DeleteMapping("/{id}")
    public Result<Void> deletePet(@PathVariable Long id) {
        return petService.deletePet(id);
    }
}
