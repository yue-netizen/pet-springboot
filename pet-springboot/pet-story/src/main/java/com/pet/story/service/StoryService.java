package com.pet.story.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.vo.StoryDetailVO;
import com.pet.story.vo.StoryVO;

public interface StoryService extends IService<Story> {

    Result<Page<StoryDetailVO>> getStoryList(Integer page, Integer size);

    Result<StoryDetailVO> getStoryById(Long id, Long userId);

    Result<Void> createStory(StoryVO storyVO, Long userId);

    Result<Void> updateStory(Story story);

    Result<Void> deleteStory(Long id, Long userId);

    Result<Void> likeStory(Long storyId, Long userId);

    Result<Page<Story>> getMyStories(Long userId, Integer page, Integer size);

    Result<Page<StoryDetailVO>> getMyLikedStories(Long userId, Integer page, Integer size);
}
