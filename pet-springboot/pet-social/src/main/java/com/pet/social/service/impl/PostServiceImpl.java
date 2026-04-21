package com.pet.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.constant.RedisConstants;
import com.pet.common.dto.UserDTO;
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
import com.pet.common.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final com.pet.social.service.TopicService topicService;
    private final UserFeignClient userFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    private void fillUserInfo(List<Post> posts) {
        if (posts == null || posts.isEmpty()) return;

        Set<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, UserDTO> userMap = new HashMap<>();
        for (Long uid : userIds) {
            try {
                Result<UserDTO> result = userFeignClient.getUserById(uid);
                if (result != null && result.getData() != null) {
                    userMap.put(uid, result.getData());
                }
            } catch (Exception e) {
                log.error("获取用户信息失败, userId: {}", uid, e);
            }
        }

        for (Post post : posts) {
            UserDTO user = userMap.get(post.getUserId());
            if (user != null) {
                post.setUserNickname(user.getNickname() != null ? user.getNickname() : "用户" + post.getUserId());
                post.setUserAvatar(user.getAvatar());
            } else {
                post.setUserNickname("用户" + post.getUserId());
            }
        }
    }

    private void fillUserInfo(Post post) {
        if (post == null || post.getUserId() == null) return;

        try {
            Result<UserDTO> result = userFeignClient.getUserById(post.getUserId());
            if (result != null && result.getData() != null) {
                UserDTO user = result.getData();
                post.setUserNickname(user.getNickname() != null ? user.getNickname() : "用户" + post.getUserId());
                post.setUserAvatar(user.getAvatar());
            } else {
                post.setUserNickname("用户" + post.getUserId());
            }
        } catch (Exception e) {
            log.error("获取用户信息失败, userId: {}", post.getUserId(), e);
            post.setUserNickname("用户" + post.getUserId());
        }
    }

    @Override
    public Result<Page<Post>> getPostList(Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime);

        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result = this.page(postPage, wrapper);

        if (userId != null) {
            for (Post post : result.getRecords()) {
                String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + post.getId();
                Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
                post.setLiked(Boolean.TRUE.equals(isMember));
            }
        }

        fillUserInfo(result.getRecords());

        return Result.success(result);
    }

    @Override
    public Result<Post> getPostById(Long id, Long userId) {
        String cacheKey = RedisConstants.SOCIAL_POST_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        Post post;
        if (cached != null) {
            post = (Post) cached;
            log.info("命中帖子缓存, postId={}", id);
        } else {
            post = this.getById(id);
            if (post == null) {
                throw BusinessException.of("帖子不存在");
            }
            redisTemplate.opsForValue().set(cacheKey, post, RedisConstants.SOCIAL_POST_CACHE_TIME, TimeUnit.SECONDS);
        }

        if (userId != null) {
            String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + id;
            Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
            post.setLiked(Boolean.TRUE.equals(isMember));
        }

        fillUserInfo(post);

        return Result.success(post);
    }

    @Override
    public Result<Void> createPost(PostVO postVO, Long userId) {
        Map<String, Object> message = new HashMap<>();
        message.put("postVO", postVO);
        message.put("userId", userId);

        rabbitTemplate.convertAndSend("social.exchange", "social.post.create", message);
        log.info("发帖请求已发送至MQ异步处理, userId={}", userId);

        return Result.success();
    }

    public void handleCreatePost(PostVO postVO, Long userId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(postVO.getTitle());
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

        if (postVO.getTags() != null && !postVO.getTags().isEmpty()) {
            String[] tags = postVO.getTags().split(",");
            for (String tag : tags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    topicService.createTopic(tag.trim());
                    topicService.incrementTopicUseCount(tag.trim());
                }
            }
        }

        log.info("帖子创建完成, postId={}, userId={}", post.getId(), userId);
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

        String cacheKey = RedisConstants.SOCIAL_POST_KEY + id;
        redisTemplate.delete(cacheKey);

        String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + id;
        redisTemplate.delete(likeKey);

        String commentCacheKey = RedisConstants.SOCIAL_POST_COMMENT_KEY + id;
        redisTemplate.delete(commentCacheKey);

        log.info("帖子已删除并清除缓存, postId={}", id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likePost(Long postId, Long userId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }

        String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + postId;
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
        if (Boolean.TRUE.equals(isMember)) {
            throw BusinessException.of("已点赞过该帖子");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(postId);
        like.setTargetType(1);
        likeMapper.insert(like);

        post.setLikeCount(post.getLikeCount() + 1);
        this.updateById(post);

        redisTemplate.opsForSet().add(likeKey, userId);

        String cacheKey = RedisConstants.SOCIAL_POST_KEY + postId;
        redisTemplate.delete(cacheKey);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unlikePost(Long postId, Long userId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }

        String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + postId;
        Long removed = redisTemplate.opsForSet().remove(likeKey, userId);

        if (removed != null && removed > 0) {
            LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Like::getUserId, userId)
                    .eq(Like::getTargetId, postId)
                    .eq(Like::getTargetType, 1);
            likeMapper.delete(wrapper);

            if (post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                this.updateById(post);
            }
        }

        String cacheKey = RedisConstants.SOCIAL_POST_KEY + postId;
        redisTemplate.delete(cacheKey);

        return Result.success();
    }

    @Override
    public Result<Boolean> checkPostLiked(Long postId, Long userId) {
        String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + postId;
        Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);

        if (isMember != null) {
            return Result.success(isMember);
        }

        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, postId)
                .eq(Like::getTargetType, 1);
        long count = likeMapper.selectCount(wrapper);
        boolean liked = count > 0;

        if (liked) {
            redisTemplate.opsForSet().add(likeKey, userId);
        }

        return Result.success(liked);
    }

    @Override
    public Result<Page<Post>> getMyPosts(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreateTime);

        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result = this.page(postPage, wrapper);

        for (Post post : result.getRecords()) {
            post.setLiked(true);
        }

        fillUserInfo(result.getRecords());

        return Result.success(result);
    }

    @Override
    public Result<Page<Post>> getMyLikedPosts(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, 1)
                .orderByDesc(Like::getCreateTime);

        Page<Like> likePage = new Page<>(page, size);
        Page<Like> likes = likeMapper.selectPage(likePage, likeWrapper);

        if (likes.getRecords().isEmpty()) {
            return Result.success(new Page<>(page, size, 0));
        }

        List<Long> postIds = likes.getRecords().stream()
                .map(Like::getTargetId)
                .toList();

        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getId, postIds);
        List<Post> posts = this.list(postWrapper);
        Map<Long, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));

        List<Post> orderedPosts = postIds.stream()
                .filter(postMap::containsKey)
                .map(postMap::get)
                .peek(p -> p.setLiked(true))
                .toList();

        Page<Post> resultPage = new Page<>(page, size, likes.getTotal());
        resultPage.setRecords(orderedPosts);

        fillUserInfo(orderedPosts);

        return Result.success(resultPage);
    }

    @Override
    public Result<Page<Post>> searchPosts(String keyword, Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                .and(w -> w.like(Post::getTitle, keyword)
                        .or().like(Post::getContent, keyword)
                        .or().like(Post::getTags, keyword))
                .orderByDesc(Post::getCreateTime);

        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result = this.page(postPage, wrapper);

        Set<Long> postUserIds = result.getRecords().stream()
                .map(Post::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        try {
            List<UserDTO> matchedUsers = userFeignClient.searchUsers(keyword);
            if (matchedUsers != null) {
                for (UserDTO u : matchedUsers) {
                    if (!postUserIds.contains(u.getId())) {
                        LambdaQueryWrapper<Post> userWrapper = new LambdaQueryWrapper<>();
                        userWrapper.eq(Post::getUserId, u.getId())
                                .eq(Post::getStatus, 1)
                                .orderByDesc(Post::getCreateTime);
                        List<Post> userPosts = this.list(userWrapper);
                        for (Post p : userPosts) {
                            p.setUserNickname(u.getNickname() != null ? u.getNickname() : "用户" + p.getUserId());
                            p.setUserAvatar(u.getAvatar());
                        }
                        result.getRecords().addAll(userPosts);
                    }
                }
            }
        } catch (Exception e) {
            log.error("搜索用户失败", e);
        }

        if (userId != null) {
            for (Post post : result.getRecords()) {
                String likeKey = RedisConstants.SOCIAL_POST_LIKE_KEY + post.getId();
                Boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
                post.setLiked(Boolean.TRUE.equals(isMember));
            }
        }

        fillUserInfo(result.getRecords());

        return Result.success(result);
    }
}
