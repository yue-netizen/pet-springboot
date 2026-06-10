package com.pet.tips.ai.service;

import com.pet.tips.ai.entity.AiKnowledge;
import com.pet.tips.ai.service.AiKnowledgeService;
import com.pet.tips.ai.vectorstore.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipsVectorStoreInitService implements ApplicationRunner {

    private final InMemoryVectorStore inMemoryVectorStore;
    private final AiKnowledgeService aiKnowledgeService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始加载AI知识库数据到向量数据库...");
            var listResult = aiKnowledgeService.listEnabled();
            List<AiKnowledge> knowledgeList = listResult.getData();

            if (knowledgeList == null || knowledgeList.isEmpty()) {
                log.warn("AI知识库数据为空，跳过向量库初始化（请通过 /ai-knowledge 接口添加知识条目后重启服务）");
                return;
            }

            List<Document> documents = knowledgeList.stream()
                    .map(k -> {
                        String content = String.format("【标题】%s\n【分类】%s\n【内容】%s",
                                k.getTitle(), k.getCategory(), k.getContent());
                        return new Document(content, Map.of(
                                "id", String.valueOf(k.getId()),
                                "title", k.getTitle() != null ? k.getTitle() : "",
                                "category", k.getCategory() != null ? k.getCategory() : "",
                                "source", k.getSource() != null ? k.getSource() : ""
                        ));
                    })
                    .collect(Collectors.toList());

            inMemoryVectorStore.add(documents);
            log.info("AI知识库向量库初始化完成，共加载 {} 条数据", documents.size());
        } catch (Exception e) {
            log.error("AI知识库向量库初始化失败（不影响服务启动，重启服务后重试）: {}", e.getMessage());
        }
    }
}
