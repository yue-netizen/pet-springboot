package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Post;
import com.pet.admin.service.PostService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员帖子管理", description = "管理员帖子管理接口")
@RestController
@RequestMapping("/admin/post")
public class AdminPostController {

    @Resource
    private PostService postService;

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public Result<Page<Post>> getPostList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return postService.getPostList(page, size);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }
}
