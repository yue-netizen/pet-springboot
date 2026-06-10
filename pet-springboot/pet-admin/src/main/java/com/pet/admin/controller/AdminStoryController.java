package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Story;
import com.pet.admin.service.StoryService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员故事管理", description = "管理员故事管理接口")
@RestController
@RequestMapping("/admin/story")
public class AdminStoryController {

    @Resource
    private StoryService storyService;

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

    @Operation(summary = "新增故事")
    @PostMapping
    public Result<Void> addStory(@RequestBody Story story) {
        return storyService.addStory(story);
    }

    @Operation(summary = "更新故事")
    @PutMapping
    public Result<Void> updateStory(@RequestBody Story story) {
        return storyService.updateStory(story);
    }

    @Operation(summary = "删除故事")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStory(@PathVariable Long id) {
        return storyService.deleteStory(id);
    }
}
