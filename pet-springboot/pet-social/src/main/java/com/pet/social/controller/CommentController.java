package com.pet.social.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.social.entity.Comment;
import com.pet.social.service.CommentService;
import com.pet.social.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "获取帖子评论列表")
    @GetMapping("/post/{postId}")
    public Result<Page<Comment>> getCommentsByPostId(@PathVariable Long postId,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsByPostId(postId, page, size);
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Void> createComment(@Valid @RequestBody CommentVO commentVO,
                                       @RequestHeader("X-User-Id") Long userId) {
        return commentService.createComment(commentVO, userId);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long userId) {
        return commentService.deleteComment(id, userId);
    }
}
