package com.pet.chat.ai.controller;

import com.pet.chat.ai.service.AiService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "智能客服")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "发送消息给AI智能客服")
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody AiChatRequest request,
                                            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        String sessionId = request.sessionId();
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = userId != null ? "user_" + userId : "guest_" + UUID.randomUUID().toString().substring(0, 8);
        }

        log.info("AI对话请求: sessionId={}, userId={}, message={}", sessionId, userId, request.message());

        String response = aiService.chat(sessionId, request.message(), userId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", response);
        result.put("sessionId", sessionId);

        return Result.success(result);
    }

    @Operation(summary = "获取AI对话历史")
    @GetMapping("/history/{sessionId}")
    public Result<List<Map<String, String>>> getHistory(@PathVariable String sessionId) {
        log.info("获取AI对话历史: sessionId={}", sessionId);

        try {
            List<Map<String, String>> history = aiService.getChatHistory(sessionId);
            log.info("返回历史记录数量: {}", history.size());
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取对话历史失败, sessionId={}", sessionId, e);
            return Result.error("获取对话历史失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清除AI会话")
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> clearSession(@PathVariable String sessionId) {
        log.info("清除AI会话: sessionId={}", sessionId);
        aiService.clearSession(sessionId);
        return Result.success();
    }

    @Operation(summary = "获取用户的所有AI会话")
    @GetMapping("/sessions")
    public Result<List<String>> getUserSessions(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("获取用户AI会话列表: userId={}", userId);
        List<String> sessions = aiService.getUserSessions(userId);
        return Result.success(sessions);
    }

    public record AiChatRequest(String message, String sessionId) {}
}
