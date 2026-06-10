package com.pet.tips.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.result.Result;
import com.pet.tips.ai.entity.AiKnowledge;
import com.pet.tips.ai.mapper.AiKnowledgeMapper;
import com.pet.tips.ai.service.AiKnowledgeService;
import com.pet.tips.ai.vectorstore.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeServiceImpl extends ServiceImpl<AiKnowledgeMapper, AiKnowledge> implements AiKnowledgeService {

    private final InMemoryVectorStore inMemoryVectorStore;

    @Override
    public Result<IPage<AiKnowledge>> getList(Integer page, Integer size, String category, String keyword) {
        Page<AiKnowledge> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledge::getStatus, 1);
        if (StringUtils.isNotBlank(category)) {
            wrapper.like(AiKnowledge::getCategory, category);
        }
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(AiKnowledge::getTitle, keyword)
                    .or().like(AiKnowledge::getContent, keyword));
        }
        wrapper.orderByDesc(AiKnowledge::getCreateTime);
        return Result.success(this.page(pageParam, wrapper));
    }

    @Override
    public Result<AiKnowledge> getById(Long id) {
        AiKnowledge knowledge = super.getById(id);
        if (knowledge == null) {
            return Result.error("知识条目不存在");
        }
        return Result.success(knowledge);
    }

    @Override
    public Result<Void> create(AiKnowledge knowledge) {
        knowledge.setStatus(1);
        this.save(knowledge);

        try {
            Document doc = toDocument(knowledge);
            inMemoryVectorStore.add(doc);
            log.info("新增知识[{}]已同步到向量库, 当前总量: {}", knowledge.getTitle(), inMemoryVectorStore.size());
        } catch (Exception e) {
            log.warn("新增知识[{}]向量同步失败，不影响数据保存，下次重启将自动修复: {}", knowledge.getTitle(), e.getMessage());
        }

        return Result.success();
    }

    @Override
    public Result<Void> update(Long id, AiKnowledge knowledge) {
        AiKnowledge existing = super.getById(id);
        if (existing == null) {
            return Result.error("知识条目不存在");
        }
        knowledge.setId(id);
        this.updateById(knowledge);

        rebuildVectorStore();

        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        this.removeById(id);
        rebuildVectorStore();
        return Result.success();
    }

    @Override
    public Result<List<AiKnowledge>> listEnabled() {
        LambdaQueryWrapper<AiKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledge::getStatus, 1);
        wrapper.orderByDesc(AiKnowledge::getCreateTime);
        return Result.success(this.list(wrapper));
    }

    private void rebuildVectorStore() {
        try {
            var listResult = listEnabled();
            List<AiKnowledge> knowledgeList = listResult.getData();
            if (knowledgeList == null || knowledgeList.isEmpty()) {
                inMemoryVectorStore.clear();
                return;
            }
            List<Document> documents = knowledgeList.stream()
                    .map(this::toDocument)
                    .collect(Collectors.toList());
            inMemoryVectorStore.rebuild(documents);
            log.info("向量库已重建，共 {} 条知识", documents.size());
        } catch (Exception e) {
            log.warn("向量库重建失败，下次启动将自动恢复: {}", e.getMessage());
        }
    }

    private Document toDocument(AiKnowledge k) {
        String content = String.format("【标题】%s\n【分类】%s\n【内容】%s",
                k.getTitle(), k.getCategory(), k.getContent());
        return new Document(content, Map.of(
                "id", String.valueOf(k.getId()),
                "title", k.getTitle() != null ? k.getTitle() : "",
                "category", k.getCategory() != null ? k.getCategory() : "",
                "source", k.getSource() != null ? k.getSource() : ""
        ));
    }
}
