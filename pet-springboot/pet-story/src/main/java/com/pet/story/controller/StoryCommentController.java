package com.pet.story.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.story.service.StoryCommentService;
import com.pet.story.vo.StoryCommentDetailVO;
import com.pet.story.vo.StoryCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "故事评论管理")
@RestController
@RequestMapping("/story-comment")
@RequiredArgsConstructor
public class StoryCommentController {

    private final StoryCommentService storyCommentService;

    @Operation(summary = "获取故事评论列表")
    @GetMapping("/story/{storyId}")
    public Result<Page<StoryCommentDetailVO>> getCommentsByStoryId(@PathVariable Long storyId,
                                                                     @RequestParam(defaultValue = "1") Integer page,
                                                                     @RequestParam(defaultValue = "10") Integer size) {
        return storyCommentService.getCommentsByStoryId(storyId, page, size);
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Void> createComment(@Valid @RequestBody StoryCommentVO commentVO,
                                       @RequestHeader("X-User-Id") Long userId) {
        return storyCommentService.createComment(commentVO, userId);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long userId) {
        return storyCommentService.deleteComment(id, userId);
    }
}
