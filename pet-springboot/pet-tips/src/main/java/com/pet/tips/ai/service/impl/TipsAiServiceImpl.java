package com.pet.tips.ai.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.pet.common.constant.RedisConstants;
import com.pet.tips.ai.service.TipsAiService;
import com.pet.tips.ai.tool.WebSearchTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipsAiServiceImpl implements TipsAiService {

    private final ChatClient tipsChatClient;
    private final WebSearchTool webSearchTool;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String ask(String breed, String question) {
        log.info("养宠AI助手请求: breed={}, question={}", breed, question);

        String cacheKey = RedisConstants.TIPS_AI_ANSWER_KEY + DigestUtil.md5Hex(breed + "::" + question);
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            log.info("命中AI助手缓存，直接返回");
            return (String) cached;
        }

        String userMessage = String.format("""
                宠物品种：%s
                用户问题：%s

                请根据用户提供的宠物品种和问题，给出专业的养宠建议。
                你拥有联网搜索能力(webSearch工具)，请直接使用它获取最新、最准确的养宠知识来回答用户。
                """, breed, question);

        try {
            String response = tipsChatClient.prompt()
                    .user(userMessage)
                    .tools(webSearchTool)
                    .call()
                    .content();

            log.info("AI回复成功，长度: {}", response != null ? response.length() : 0);

            redisTemplate.opsForValue().set(cacheKey, response, RedisConstants.TIPS_AI_CACHE_TIME, TimeUnit.SECONDS);

            return response;
        } catch (Exception e) {
            log.error("AI请求失败", e);
            return "抱歉，AI助手暂时无法响应，请稍后再试～🐾";
        }
    }
}
