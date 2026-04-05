package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.Pet;
import com.pet.common.result.Result;

public interface PetService extends IService<Pet> {

    Result<Page<Pet>> getPetList(Integer page, Integer size);

    Result<Pet> getPetById(Long id);

    Result<Void> addPet(Pet pet);

    Result<Void> updatePet(Pet pet);

    Result<Void> deletePet(Long id);
}
