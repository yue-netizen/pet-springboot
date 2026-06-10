package com.pet.chat.ai.context;

public class RequestContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getUserId() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }
}
