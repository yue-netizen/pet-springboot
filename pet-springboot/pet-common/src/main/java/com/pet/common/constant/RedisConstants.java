package com.pet.common.constant;

public interface RedisConstants {

    String USER_TOKEN_KEY = "pet:user:token:";
    String USER_INFO_KEY = "pet:user:info:";
    String PET_DETAIL_KEY = "pet:pet:detail:";
    String SOCIAL_POST_KEY = "pet:social:post:";
    String SOCIAL_POST_COMMENT_KEY = "pet:social:comment:";
    String SOCIAL_POST_LIKE_KEY = "pet:social:like:";
    String CHAT_UNREAD_KEY = "pet:chat:unread:";
    String TIPS_AI_ANSWER_KEY = "pet:tips:ai:";
    long TOKEN_EXPIRE_TIME = 24 * 60 * 60;
    long CACHE_EXPIRE_TIME = 30 * 60;
    long TIPS_AI_CACHE_TIME = 3 * 60 * 60;
    long SOCIAL_POST_CACHE_TIME = 30 * 60;
    long SOCIAL_COMMENT_CACHE_TIME = 10 * 60;
}
