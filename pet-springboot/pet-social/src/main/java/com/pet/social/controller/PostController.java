package com.pet.social.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.social.entity.Post;
import com.pet.social.service.PostService;
import com.pet.social.vo.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "帖子管理")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public Result<Page<Post>> getPostList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return postService.getPostList(page, size, userId);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Long id,
                                     @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return postService.getPostById(id, userId);
    }

    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<Void> createPost(@Valid @RequestBody PostVO postVO,
                                    @RequestHeader("X-User-Id") Long userId) {
        return postService.createPost(postVO, userId);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id,
                                    @RequestHeader("X-User-Id") Long userId) {
        return postService.deletePost(id, userId);
    }

    @Operation(summary = "点赞帖子")
    @PostMapping("/{id}/like")
    public Result<Void> likePost(@PathVariable Long id,
                                  @RequestHeader("X-User-Id") Long userId) {
        return postService.likePost(id, userId);
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikePost(@PathVariable Long id,
                                    @RequestHeader("X-User-Id") Long userId) {
        return postService.unlikePost(id, userId);
    }

    @Operation(summary = "检查用户是否点赞")
    @GetMapping("/{id}/liked")
    public Result<Boolean> checkPostLiked(@PathVariable Long id,
                                           @RequestHeader("X-User-Id") Long userId) {
        return postService.checkPostLiked(id, userId);
    }

    @Operation(summary = "获取我的帖子")
    @GetMapping("/my")
    public Result<Page<Post>> getMyPosts(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestHeader("X-User-Id") Long userId) {
        return postService.getMyPosts(userId, page, size);
    }

    @Operation(summary = "获取用户帖子列表")
    @GetMapping("/user/{userId}")
    public Result<Page<Post>> getUserPosts(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return postService.getMyPosts(userId, page, size);
    }

    @Operation(summary = "获取我点赞的帖子")
    @GetMapping("/my/liked")
    public Result<Page<Post>> getMyLikedPosts(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestHeader("X-User-Id") Long userId) {
        return postService.getMyLikedPosts(userId, page, size);
    }
}
