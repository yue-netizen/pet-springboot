package com.pet.pet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import com.pet.pet.entity.Pet;
import com.pet.pet.vo.AdoptionApplyVO;
import com.pet.pet.vo.PetQueryVO;

public interface PetService extends IService<Pet> {

    Result<Page<Pet>> getPetList(PetQueryVO queryVO);

    Result<PetDTO> getPetById(Long id);

    Result<Void> addPet(Pet pet);

    Result<Void> updatePet(Pet pet);

    Result<Void> deletePet(Long id);

    Result<Void> applyAdoption(AdoptionApplyVO applyVO, Long userId);

    Result<Page<Pet>> getMyAdoptions(Long userId, Integer page, Integer size);
}
