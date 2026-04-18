package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Story;
import com.pet.admin.mapper.StoryMapper;
import com.pet.admin.service.StoryService;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import org.springframework.stereotype.Service;

@Service
public class StoryServiceImpl extends ServiceImpl<StoryMapper, Story> implements StoryService {

    @Override
    public Result<Page<Story>> getStoryList(Integer page, Integer size) {
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Story::getCreateTime);

        Page<Story> storyPage = new Page<>(page, size);
        Page<Story> result = this.page(storyPage, wrapper);

        return Result.success(result);
    }

    @Override
    public Result<Story> getStoryById(Long id) {
        Story story = this.getById(id);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }
        return Result.success(story);
    }

    @Override
    public Result<Void> addStory(Story story) {
        story.setLikeCount(0);
        story.setViewCount(0);
        story.setStatus(1);
        this.save(story);
        return Result.success();
    }

    @Override
    public Result<Void> updateStory(Story story) {
        Story existing = this.getById(story.getId());
        if (existing == null) {
            throw BusinessException.of("故事不存在");
        }
        this.updateById(story);
        return Result.success();
    }

    @Override
    public Result<Void> deleteStory(Long id) {
        Story story = this.getById(id);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }
        this.removeById(id);
        return Result.success();
    }
}
