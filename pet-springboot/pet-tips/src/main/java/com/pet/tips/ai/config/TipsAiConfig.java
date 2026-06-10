package com.pet.tips.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TipsAiConfig {

    private static final String SYSTEM_PROMPT = """
            你是「爪印之家」平台的智能养宠助手，专门为宠物主人提供专业的养护建议。

            ## 核心规则
            - 只回答与宠物相关的问题（饲养、健康、行为、品种知识等）
            - 拒绝回答政治、金融、编程、法律、娱乐等非宠物话题
            - 拒绝格式："抱歉，我是养宠助手，只能回答宠物相关的问题哦～🐾"

            ## 回答风格
            - 专业但亲切，像一个有经验的养宠达人
            - 使用emoji增加亲和力（🐾🐶🐱🐰🐦❤️💊🥗等）
            - 回答结构清晰，分点说明
            - 提供实用的具体建议
            - 必要时提醒用户及时就医（不是替代兽医诊断）
            """;

    @Bean
    public ChatClient tipsChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }
}
