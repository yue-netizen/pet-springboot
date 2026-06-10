package com.pet.tips.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class WebSearchTool {

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool(description = """
            联网搜索宠物相关的最新信息。当向量库中没有足够相关信息时调用此工具获取最新养宠知识。
            
            适用场景：
            - 需要最新的宠物医疗研究成果
            - 用户询问罕见品种的饲养方法
            - 需要查询最新的宠物食品/用品推荐
            - 需要了解宠物疾病的最新治疗方法
            
            搜索建议：使用简短的关键词组合，如"布偶猫 软便 治疗"、"金毛 掉毛 原因"
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
                                    .fluentPut("content", "你是搜索引擎。根据用户搜索词，返回最相关的搜索结果摘要，简洁专业，用中文回答。"),
                            new JSONObject().fluentPut("role", "user")
                                    .fluentPut("content", "搜索：" + query)
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
            return "联网搜索暂时失败";
        }
    }
}
