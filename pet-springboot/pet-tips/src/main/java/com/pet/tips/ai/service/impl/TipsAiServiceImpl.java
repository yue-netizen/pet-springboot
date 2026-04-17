package com.pet.tips.ai.service.impl;

import com.pet.tips.ai.service.TipsAiService;
import com.pet.tips.ai.tool.WebSearchTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipsAiServiceImpl implements TipsAiService {

    private final ChatClient tipsChatClient;
    private final WebSearchTool webSearchTool;

    @Override
    public String ask(String breed, String question) {
        log.info("养宠AI助手请求: breed={}, question={}", breed, question);

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
            return response;
        } catch (Exception e) {
            log.error("AI请求失败", e);
            return "抱歉，AI助手暂时无法响应，请稍后再试～🐾";
        }
    }
}
