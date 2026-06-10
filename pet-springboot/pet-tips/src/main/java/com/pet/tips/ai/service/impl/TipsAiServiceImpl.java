package com.pet.tips.ai.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.pet.common.constant.RedisConstants;
import com.pet.tips.ai.service.TipsAiService;
import com.pet.tips.ai.tool.WebSearchTool;
import com.pet.tips.ai.vectorstore.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipsAiServiceImpl implements TipsAiService {

    private final ChatClient tipsChatClient;
    private final WebSearchTool webSearchTool;
    private final RedisTemplate<String, Object> redisTemplate;
    private final InMemoryVectorStore inMemoryVectorStore;

    private static final int VECTOR_SEARCH_TOP_K = 3;
    private static final double VECTOR_SEARCH_MIN_SCORE = 0.3;

    @Override
    public String ask(String breed, String question) {
        log.info("养宠AI助手请求: breed={}, question={}", breed, question);

        String cacheKey = RedisConstants.TIPS_AI_ANSWER_KEY + DigestUtil.md5Hex(breed + "::" + question);

        String cached = tryGetFromCache(cacheKey);
        if (cached != null) {
            return cached;
        }

        String query = buildQuery(breed, question);

        String answer = tryVectorStoreRAG(query, breed, question, cacheKey);
        if (answer != null) {
            return answer;
        }

        log.info("向量库未找到足够相关信息，降级到联网搜索");
        answer = tryWebSearchFallback(query, breed, question, cacheKey);
        return answer;
    }

    private String tryGetFromCache(String cacheKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("命中AI助手缓存，直接返回");
                return (String) cached;
            }
        } catch (Exception e) {
            log.warn("读取缓存异常: {}", e.getMessage());
        }
        return null;
    }

    private String tryVectorStoreRAG(String query, String breed, String question, String cacheKey) {
        try {
            long startTime = System.currentTimeMillis();
            List<Document> similarDocs = inMemoryVectorStore.similaritySearch(
                    query, VECTOR_SEARCH_TOP_K, VECTOR_SEARCH_MIN_SCORE
            );
            long searchCost = System.currentTimeMillis() - startTime;
            log.info("向量库检索完成, 耗时{}ms, 命中{}条文档", searchCost, similarDocs.size());

            if (similarDocs.isEmpty()) {
                log.info("向量库无匹配结果");
                return null;
            }

            String contextText = buildContextFromDocuments(similarDocs);
            String enhancedPrompt = buildRAGPrompt(breed, question, contextText);

            String response = tipsChatClient.prompt()
                    .user(enhancedPrompt)
                    .call()
                    .content();

            log.info("向量库RAG生成回答成功, 总耗时{}ms", System.currentTimeMillis() - startTime);
            saveToCache(cacheKey, response);
            return response;

        } catch (Exception e) {
            log.error("向量库RAG处理失败, 降级到联网搜索: {}", e.getMessage());
            return null;
        }
    }

    private String tryWebSearchFallback(String query, String breed, String question, String cacheKey) {
        try {
            long startTime = System.currentTimeMillis();
            String userMessage = String.format("""
                    宠物品种：%s
                    用户问题：%s

                    请根据用户提供的宠物品种和问题，给出专业的养宠建议。
                    你拥有联网搜索能力(webSearch工具)，请直接使用它获取最新、最准确的养宠知识来回答用户。
                    """, breed, question);

            String response = tipsChatClient.prompt()
                    .user(userMessage)
                    .tools(webSearchTool)
                    .call()
                    .content();

            log.info("联网搜索降级回答成功, 耗时{}ms", System.currentTimeMillis() - startTime);
            saveToCache(cacheKey, response);
            return response;

        } catch (Exception e) {
            log.error("联网搜索也失败了", e);
            return "抱歉，AI助手暂时无法响应，请稍后再试～🐾";
        }
    }

    private String buildQuery(String breed, String question) {
        return String.format("%s %s 养护 饲养 建议", breed, question);
    }

    private String buildContextFromDocuments(List<Document> documents) {
        return documents.stream()
                .map(doc -> {
                    Map<String, Object> metadata = doc.getMetadata();
                    String title = metadata.getOrDefault("title", "未知标题").toString();
                    return String.format("《%s》\n%s", title, doc.getText());
                })
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    private String buildRAGPrompt(String breed, String question, String contextText) {
        return String.format("""
                你是「爪印之家」平台的智能养宠助手。请根据以下【参考资料】回答用户问题。

                【参考资料】
                %s

                【用户问题】
                宠物品种：%s
                具体问题：%s

                要求：
                - 主要基于参考资料回答，如果资料不足以完全解答，结合你的专业知识补充
                - 专业但亲切，像一个有经验的养宠达人
                - 使用emoji增加亲和力（🐾🐶🐱🐰等）
                - 回答结构清晰，分点说明
                - 必要时提醒用户及时就医（不是替代兽医诊断）
                """, contextText, breed, question);
    }

    private void saveToCache(String cacheKey, String response) {
        try {
            redisTemplate.opsForValue().set(cacheKey, response,
                    RedisConstants.TIPS_AI_CACHE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("保存缓存失败: {}", e.getMessage());
        }
    }
}
