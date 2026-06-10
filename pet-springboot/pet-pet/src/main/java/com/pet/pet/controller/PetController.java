package com.pet.pet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import com.pet.pet.entity.Pet;
import com.pet.pet.service.PetService;
import com.pet.pet.vo.AdoptionApplyVO;
import com.pet.pet.vo.PetQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物管理")
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @Operation(summary = "获取宠物列表")
    @GetMapping("/list")
    public Result<Page<Pet>> getPetList(PetQueryVO queryVO) {
        return petService.getPetList(queryVO);
    }

    @Operation(summary = "获取宠物详情")
    @GetMapping("/{id}")
    public Result<PetDTO> getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @Operation(summary = "添加宠物")
    @PostMapping
    public Result<Void> addPet(@RequestBody Pet pet) {
        return petService.addPet(pet);
    }

    @Operation(summary = "更新宠物信息")
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
