package com.pet.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.social.entity.Comment;
import com.pet.social.entity.Like;
import com.pet.social.entity.Post;
import com.pet.social.mapper.CommentMapper;
import com.pet.social.mapper.LikeMapper;
import com.pet.social.mapper.PostMapper;
import com.pet.social.service.PostService;
import com.pet.social.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;

    @Override
    public Result<Page<Post>> getPostList(Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime);
        
        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result = this.page(postPage, wrapper);
        
        if (userId != null) {
            for (Post post : result.getRecords()) {
                LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
                likeWrapper.eq(Like::getUserId, userId)
                        .eq(Like::getTargetId, post.getId())
                        .eq(Like::getTargetType, 1);
                long count = likeMapper.selectCount(likeWrapper);
                post.setLiked(count > 0);
            }
        }
        
        return Result.success(result);
    }

    @Override
    public Result<Post> getPostById(Long id, Long userId) {
        Post post = this.getById(id);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        
        if (userId != null) {
            LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(Like::getUserId, userId)
                    .eq(Like::getTargetId, post.getId())
                    .eq(Like::getTargetType, 1);
            long count = likeMapper.selectCount(likeWrapper);
            post.setLiked(count > 0);
        }
        
        return Result.success(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createPost(PostVO postVO, Long userId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(postVO.getContent());
        post.setImage(postVO.getImage());
        post.setImages(postVO.getImages());
        post.setVideo(postVO.getVideo());
        post.setVideos(postVO.getVideos());
        post.setTags(postVO.getTags());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        
        this.save(post);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deletePost(Long id, Long userId) {
        Post post = this.getById(id);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        
        if (!post.getUserId().equals(userId)) {
            throw BusinessException.of("无权删除该帖子");
        }
        
        this.removeById(id);
        
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getPostId, id);
        commentMapper.delete(commentWrapper);
        
        LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(Like::getTargetId, id).eq(Like::getTargetType, 1);
        likeMapper.delete(likeWrapper);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likePost(Long postId, Long userId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, postId)
                .eq(Like::getTargetType, 1);
        
        if (likeMapper.selectCount(wrapper) > 0) {
            throw BusinessException.of("已点赞过该帖子");
        }
        
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(postId);
        like.setTargetType(1);
        likeMapper.insert(like);
        
        post.setLikeCount(post.getLikeCount() + 1);
        this.updateById(post);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unlikePost(Long postId, Long userId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, postId)
                .eq(Like::getTargetType, 1);
        
        int deleted = likeMapper.delete(wrapper);
        if (deleted > 0 && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            this.updateById(post);
        }
        
        return Result.success();
    }

    @Override
    public Result<Boolean> checkPostLiked(Long postId, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, postId)
                .eq(Like::getTargetType, 1);
        
        long count = likeMapper.selectCount(wrapper);
        return Result.success(count > 0);
    }
}
