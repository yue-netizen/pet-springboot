package com.pet.social.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pet.common.result.Result;
import com.pet.social.entity.Topic;
import com.pet.social.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "话题管理")
@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "获取热门话题")
    @GetMapping("/trending")
    public Result<IPage<Topic>> getTrendingTopics(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return topicService.getTrendingTopics(page, size);
    }

    @Operation(summary = "搜索话题")
    @GetMapping("/search")
    public Result<IPage<Topic>> searchTopics(@RequestParam String keyword,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return topicService.searchTopics(keyword, page, size);
    }

    @Operation(summary = "创建话题")
    @PostMapping
    public Result<Topic> createTopic(@RequestParam String name) {
        return topicService.createTopic(name);
    }
}
