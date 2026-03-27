package com.pet.common.constant;

public interface RedisConstants {

    String USER_TOKEN_KEY = "pet:user:token:";
    String USER_INFO_KEY = "pet:user:info:";
    String PET_DETAIL_KEY = "pet:pet:detail:";
    String SOCIAL_POST_KEY = "pet:social:post:";
    String CHAT_UNREAD_KEY = "pet:chat:unread:";
    long TOKEN_EXPIRE_TIME = 24 * 60 * 60;
    long CACHE_EXPIRE_TIME = 30 * 60;
}
