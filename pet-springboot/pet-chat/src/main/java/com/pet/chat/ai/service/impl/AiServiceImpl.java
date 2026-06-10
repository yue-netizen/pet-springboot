package com.pet.chat.ai.service.impl;

import com.pet.chat.ai.context.RequestContext;
import com.pet.chat.ai.entity.AiChatHistory;
import com.pet.chat.ai.mapper.AiChatHistoryMapper;
import com.pet.chat.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final AiChatHistoryMapper aiChatHistoryMapper;

    @Override
    public String chat(String sessionId, String userMessage, Long userId) {
        try {
            log.info("开始AI对话: sessionId={}, userId={}, message={}", sessionId, userId, userMessage);

            RequestContext.setUserId(userId);
            
            String response = chatClient.prompt()
                    .user(userMessage)
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
                    .call()
                    .content();

            log.info("AI原始响应: {}", response);

            if (response != null && !response.isBlank()) {
                saveMessageToDb(sessionId, userId, "user", userMessage);
                saveMessageToDb(sessionId, userId, "assistant", response);

                List<AiChatHistory> history = aiChatHistoryMapper.findBySessionId(sessionId);
                if (history.size() > 40) {
                    List<AiChatHistory> toDelete = history.subList(0, history.size() - 40);
                    toDelete.forEach(msg -> aiChatHistoryMapper.deleteById(msg.getId()));
                }

                log.info("AI对话成功: sessionId={}, responseLength={}", sessionId, response.length());
                return response;
            } else {
                log.warn("AI返回空响应: sessionId={}", sessionId);
                return "抱歉，小爪暂时无法回答您的问题，请稍后再试 🙏";
            }
        } catch (Exception e) {
            log.error("AI对话异常, sessionId={}, userId={}, errorType={}", sessionId, userId, e.getClass().getName(), e);
            log.error("AI对话异常详情: {}", e.getMessage());
            return "抱歉，小爪暂时无法回答您的问题 (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")，请稍后再试 🙏";
        } finally {
            RequestContext.clear();
        }
    }

    private void saveMessageToDb(String sessionId, Long userId, String role, String content) {
        AiChatHistory history = new AiChatHistory();
        history.setSessionId(sessionId);
        history.setUserId(userId);
        history.setRole(role);
        history.setContent(content);
        history.setCreatedAt(LocalDateTime.now());
        aiChatHistoryMapper.insert(history);
    }

    private static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id";

    @Override
    public List<Map<String, String>> getChatHistory(String sessionId) {
        try {
            log.debug("获取会话历史: sessionId={}", sessionId);

            List<AiChatHistory> dbHistory = aiChatHistoryMapper.findBySessionId(sessionId);
            if (dbHistory == null || dbHistory.isEmpty()) {
                log.info("会话历史为空: sessionId={}", sessionId);
                return new ArrayList<>();
            }

            List<Map<String, String>> result = dbHistory.stream()
                    .map(msg -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("role", msg.getRole());
                        map.put("content", msg.getContent());
                        return map;
                    })
                    .collect(Collectors.toList());

            log.info("返回会话历史: sessionId={}, count={}", sessionId, result.size());
            return result;
        } catch (Exception e) {
            log.error("获取会话历史失败, sessionId={}", sessionId, e);
            throw new RuntimeException("获取会话历史失败", e);
        }
    }

    @Override
    public void clearSession(String sessionId) {
        try {
            aiChatHistoryMapper.deleteBySessionId(sessionId);
            chatMemory.clear(sessionId);
            log.info("已清除会话: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("清除会话失败, sessionId={}", sessionId, e);
        }
    }

    @Override
    public List<String> getUserSessions(Long userId) {
        try {
            List<AiChatHistory> histories = aiChatHistoryMapper.findByUserId(userId);
            return histories.stream()
                    .map(AiChatHistory::getSessionId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户会话列表失败, userId={}", userId, e);
            return new ArrayList<>();
        }
    }
}
