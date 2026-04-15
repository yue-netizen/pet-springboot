package com.pet.chat.ai.service;

import java.util.List;
import java.util.Map;

public interface AiService {

    String chat(String sessionId, String userMessage, Long userId);

    List<Map<String, String>> getChatHistory(String sessionId);

    void clearSession(String sessionId);

    List<String> getUserSessions(Long userId);
}
