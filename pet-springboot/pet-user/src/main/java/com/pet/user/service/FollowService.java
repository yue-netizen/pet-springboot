package com.pet.user.service;

import com.pet.common.result.Result;

public interface FollowService {

    Result<Void> follow(Long userId, Long targetUserId);

    Result<Void> unfollow(Long userId, Long targetUserId);

    Result<Boolean> checkFollow(Long userId, Long targetUserId);
}
