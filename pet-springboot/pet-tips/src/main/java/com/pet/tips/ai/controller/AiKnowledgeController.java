package com.pet.tips.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pet.common.result.Result;
import com.pet.tips.ai.entity.AiKnowledge;
import com.pet.tips.ai.service.AiKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI知识库管理")
@RestController
@RequestMapping("/ai-knowledge")
@RequiredArgsConstructor
public class AiKnowledgeController {

    private final AiKnowledgeService aiKnowledgeService;

    @Operation(summary = "分页查询知识库列表")
    @GetMapping("/list")
    public Result<IPage<AiKnowledge>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return aiKnowledgeService.getList(page, size, category, keyword);
    }

    @Operation(summary = "根据ID查询知识详情")
    @GetMapping("/{id}")
    public Result<AiKnowledge> getById(@PathVariable Long id) {
        return aiKnowledgeService.getById(id);
    }

    @Operation(summary = "新增知识条目")
    @PostMapping
    public Result<Void> create(@RequestBody AiKnowledge knowledge) {
        return aiKnowledgeService.create(knowledge);
    }

    @Operation(summary = "更新知识条目")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AiKnowledge knowledge) {
        return aiKnowledgeService.update(id, knowledge);
    }

    @Operation(summary = "删除知识条目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return aiKnowledgeService.delete(id);
    }
}
