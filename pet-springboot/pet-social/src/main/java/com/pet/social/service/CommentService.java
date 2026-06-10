package com.pet.social.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.social.entity.Comment;
import com.pet.social.vo.CommentVO;

public interface CommentService extends IService<Comment> {

    Result<Page<Comment>> getCommentsByPostId(Long postId, Integer page, Integer size);

    Result<Void> createComment(CommentVO commentVO, Long userId);

    Result<Void> deleteComment(Long id, Long userId);
}
