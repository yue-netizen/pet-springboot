package com.pet.story.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.story.entity.StoryComment;
import com.pet.story.vo.StoryCommentDetailVO;
import com.pet.story.vo.StoryCommentVO;

public interface StoryCommentService extends IService<StoryComment> {

    Result<Page<StoryCommentDetailVO>> getCommentsByStoryId(Long storyId, Integer page, Integer size);

    Result<Void> createComment(StoryCommentVO commentVO, Long userId);

    Result<Void> deleteComment(Long id, Long userId);
}
