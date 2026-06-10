package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.Adoption;
import com.pet.admin.vo.AdoptionDetailVO;
import com.pet.common.result.Result;

public interface AdoptionService extends IService<Adoption> {
    Result<Page<AdoptionDetailVO>> getAdoptionList(Integer page, Integer size, Integer status);
    Result<AdoptionDetailVO> getAdoptionDetail(Long id);
    Result<Void> reviewAdoption(Long id, Integer status);
    Result<Void> updateAdoption(Adoption adoption);
}