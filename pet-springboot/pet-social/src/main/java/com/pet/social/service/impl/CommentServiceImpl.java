package com.pet.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.dto.UserDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.feign.UserFeignClient;
import com.pet.common.result.Result;
import com.pet.social.entity.Comment;
import com.pet.social.entity.Post;
import com.pet.social.mapper.CommentMapper;
import com.pet.social.mapper.PostMapper;
import com.pet.social.service.CommentService;
import com.pet.social.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostMapper postMapper;
    private final UserFeignClient userFeignClient;

    @Override
    public Result<Page<Comment>> getCommentsByPostId(Long postId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        
        Page<Comment> commentPage = new Page<>(page, size);
        Page<Comment> result = this.page(commentPage, wrapper);
        
        fillUserInfo(result.getRecords());
        
        return Result.success(result);
    }

    private void fillUserInfo(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return;

        for (Comment comment : comments) {
            try {
                Result<UserDTO> result = userFeignClient.getUserById(comment.getUserId());
                if (result != null && result.getData() != null) {
                    UserDTO user = result.getData();
                    comment.setUserNickname(user.getNickname() != null ? user.getNickname() : "用户" + comment.getUserId());
                    comment.setUserAvatar(user.getAvatar());
                } else {
                    comment.setUserNickname("用户" + comment.getUserId());
                }
            } catch (Exception e) {
                log.error("获取评论用户信息失败, userId: {}", comment.getUserId(), e);
                comment.setUserNickname("用户" + comment.getUserId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createComment(CommentVO commentVO, Long userId) {
        Post post = postMapper.selectById(commentVO.getPostId());
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        
        Comment comment = new Comment();
        comment.setPostId(commentVO.getPostId());
        comment.setUserId(userId);
        comment.setParentId(commentVO.getParentId() != null ? commentVO.getParentId() : 0L);
        comment.setContent(commentVO.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        
        this.save(comment);
        
        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.updateById(post);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteComment(Long id, Long userId) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw BusinessException.of("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId)) {
            throw BusinessException.of("无权删除该评论");
        }
        
        this.removeById(id);
        
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postMapper.updateById(post);
        }
        
        return Result.success();
    }
}
