package com.pet.tips.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.tips.entity.PetTips;
import com.pet.tips.mapper.PetTipsMapper;
import com.pet.tips.service.PetTipsService;
import com.pet.tips.vo.PetTipsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetTipsServiceImpl extends ServiceImpl<PetTipsMapper, PetTips> implements PetTipsService {

    @Override
    public Result<IPage<PetTips>> getTipsList(Integer page, Integer size, String category) {
        LambdaQueryWrapper<PetTips> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.trim().isEmpty()) {
            wrapper.eq(PetTips::getCategory, category);
        }
        wrapper.orderByDesc(PetTips::getCreateTime);
        
        Page<PetTips> tipsPage = new Page<>(page, size);
        IPage<PetTips> result = this.page(tipsPage, wrapper);
        
        return Result.success(result);
    }

    @Override
    public Result<PetTips> getTipsById(Long id) {
        PetTips tips = this.getById(id);
        if (tips == null) {
            throw BusinessException.of("贴士不存在");
        }
        return Result.success(tips);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createTips(PetTipsVO tipsVO) {
        PetTips tips = new PetTips();
        tips.setTitle(tipsVO.getTitle());
        tips.setCategory(tipsVO.getCategory());
        tips.setContent(tipsVO.getContent());
        tips.setCoverImage(tipsVO.getCoverImage());
        
        this.save(tips);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateTips(Long id, PetTipsVO tipsVO) {
        PetTips tips = this.getById(id);
        if (tips == null) {
            throw BusinessException.of("贴士不存在");
        }
        
        tips.setTitle(tipsVO.getTitle());
        tips.setCategory(tipsVO.getCategory());
        tips.setContent(tipsVO.getContent());
        tips.setCoverImage(tipsVO.getCoverImage());
        
        this.updateById(tips);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteTips(Long id) {
        PetTips tips = this.getById(id);
        if (tips == null) {
            throw BusinessException.of("贴士不存在");
        }
        
        this.removeById(id);
        
        return Result.success();
    }
}
