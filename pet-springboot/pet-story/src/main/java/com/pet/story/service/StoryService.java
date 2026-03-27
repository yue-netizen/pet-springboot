package com.pet.story.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.vo.StoryVO;

public interface StoryService extends IService<Story> {

    Result<Page<Story>> getStoryList(Integer page, Integer size);

    Result<Story> getStoryById(Long id);

    Result<Void> createStory(StoryVO storyVO, Long userId);

    Result<Void> updateStory(Story story);

    Result<Void> deleteStory(Long id, Long userId);

    Result<Void> likeStory(Long storyId);

    Result<Page<Story>> getMyStories(Long userId, Integer page, Integer size);
}
