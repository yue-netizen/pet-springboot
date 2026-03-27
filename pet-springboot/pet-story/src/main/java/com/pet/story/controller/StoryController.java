package com.pet.story.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.service.StoryService;
import com.pet.story.vo.StoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "故事管理")
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @Operation(summary = "获取故事列表")
    @GetMapping("/list")
    public Result<Page<Story>> getStoryList(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return storyService.getStoryList(page, size);
    }

    @Operation(summary = "获取故事详情")
    @GetMapping("/{id}")
    public Result<Story> getStoryById(@PathVariable Long id) {
        return storyService.getStoryById(id);
    }

    @Operation(summary = "发布故事")
    @PostMapping
    public Result<Void> createStory(@Valid @RequestBody StoryVO storyVO,
                                     @RequestHeader("X-User-Id") Long userId) {
        return storyService.createStory(storyVO, userId);
    }

    @Operation(summary = "更新故事")
    @PutMapping
    public Result<Void> updateStory(@RequestBody Story story) {
        return storyService.updateStory(story);
    }

    @Operation(summary = "删除故事")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStory(@PathVariable Long id,
                                     @RequestHeader("X-User-Id") Long userId) {
        return storyService.deleteStory(id, userId);
    }

    @Operation(summary = "点赞故事")
    @PostMapping("/{id}/like")
    public Result<Void> likeStory(@PathVariable Long id) {
        return storyService.likeStory(id);
    }

    @Operation(summary = "获取我的故事")
    @GetMapping("/my")
    public Result<Page<Story>> getMyStories(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestHeader("X-User-Id") Long userId) {
        return storyService.getMyStories(userId, page, size);
    }
}
