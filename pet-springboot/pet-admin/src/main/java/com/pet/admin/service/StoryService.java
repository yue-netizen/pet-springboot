package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Story;
import com.pet.common.result.Result;

public interface StoryService {

    Result<Page<Story>> getStoryList(Integer page, Integer size);

    Result<Story> getStoryById(Long id);

    Result<Void> addStory(Story story);

    Result<Void> updateStory(Story story);

    Result<Void> deleteStory(Long id);
}
