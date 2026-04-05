package com.pet.tips.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.tips.entity.PetTips;
import com.pet.tips.vo.PetTipsVO;

public interface PetTipsService extends IService<PetTips> {

    Result<IPage<PetTips>> getTipsList(Integer page, Integer size, String category);

    Result<PetTips> getTipsById(Long id);

    Result<Void> createTips(PetTipsVO tipsVO);

    Result<Void> updateTips(Long id, PetTipsVO tipsVO);

    Result<Void> deleteTips(Long id);
}
