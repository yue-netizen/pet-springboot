package com.pet.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.mapper.StoryMapper;
import com.pet.story.service.StoryService;
import com.pet.story.vo.StoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends ServiceImpl<StoryMapper, Story> implements StoryService {

    @Override
    public Result<Page<Story>> getStoryList(Integer page, Integer size) {
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Story::getStatus, 1)
                .orderByDesc(Story::getCreateTime);
        
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
        
        story.setViewCount(story.getViewCount() + 1);
        this.updateById(story);
        
        return Result.success(story);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createStory(StoryVO storyVO, Long userId) {
        Story story = new Story();
        story.setUserId(userId);
        story.setPetId(storyVO.getPetId());
        story.setTitle(storyVO.getTitle());
        story.setContent(storyVO.getContent());
        story.setImage(storyVO.getImage());
        story.setLikeCount(0);
        story.setViewCount(0);
        story.setStatus(1);
        
        this.save(story);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateStory(Story story) {
        Story existingStory = this.getById(story.getId());
        if (existingStory == null) {
            throw BusinessException.of("故事不存在");
        }
        
        this.updateById(story);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteStory(Long id, Long userId) {
        Story story = this.getById(id);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }
        
        if (!story.getUserId().equals(userId)) {
            throw BusinessException.of("无权删除该故事");
        }
        
        this.removeById(id);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likeStory(Long storyId) {
        Story story = this.getById(storyId);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }
        
        story.setLikeCount(story.getLikeCount() + 1);
        this.updateById(story);
        
        return Result.success();
    }

    @Override
    public Result<Page<Story>> getMyStories(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Story::getUserId, userId)
                .orderByDesc(Story::getCreateTime);
        
        Page<Story> storyPage = new Page<>(page, size);
        Page<Story> result = this.page(storyPage, wrapper);
        
        return Result.success(result);
    }
}
