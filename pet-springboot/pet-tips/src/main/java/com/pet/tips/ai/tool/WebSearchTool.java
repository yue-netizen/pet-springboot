package com.pet.tips.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSearchTool {

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool(description = """
            联网搜索宠物相关的最新信息。当用户的问题需要最新、最准确的养宠知识时调用此工具。
            
            适用场景：
            - 需要最新的宠物医疗知识或研究成果
            - 用户询问特定品种的详细饲养方法
            - 需要查询宠物食品、用品的最新推荐
            - 需要了解宠物疾病的最新治疗方法
            
            搜索建议：使用简短的关键词组合搜索效果更好，如"布偶猫 软便 治疗"、"金毛 掉毛 原因"
            """)
    public String webSearch(@ToolParam(description = "搜索关键词") String query) {
        log.info("联网搜索: query={}", query);

        try {
            String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "qwen-max-latest");
            requestBody.put("input", new JSONObject().fluentPut("messages",
                    List.of(
                            new JSONObject().fluentPut("role", "system")
                                    .fluentPut("content", "你是一个搜索引擎助手。根据用户的搜索词，返回最相关、最有用的搜索结果摘要。用中文回答，简洁明了。"),
                            new JSONObject().fluentPut("role", "user")
                                    .fluentPut("content", "请搜索以下内容并给出详细、专业的回答：\n" + query)
                    )));

            JSONObject parameters = new JSONObject();
            parameters.put("result_format", "message");
            parameters.put("enable_search", true);
            requestBody.put("parameters", parameters);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(
                    requestBody.toJSONString(), headers);

            var response = restTemplate.postForObject(url, entity, String.class);
            log.info("搜索API响应: {}", response != null ? response.substring(0, Math.min(200, response.length())) : "null");

            if (response == null || response.isEmpty()) {
                return "搜索未返回结果";
            }

            JSONObject result = JSON.parseObject(response);
            JSONObject output = result.getJSONObject("output");
            if (output == null) {
                return "搜索服务暂时不可用";
            }

            JSONArray choices = output.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "未找到相关信息";
            }

            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");

            log.info("搜索结果长度: {}", content != null ? content.length() : 0);
            return content != null ? content : "搜索无结果";

        } catch (Exception e) {
            log.error("联网搜索失败: {}", e.getMessage(), e);
            return "联网搜索暂时失败，请基于已有知识回答用户问题。错误信息：" + e.getMessage();
        }
    }
}
