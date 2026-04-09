package com.pet.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;

import java.util.List;

public interface FollowService {

    Result<Void> follow(Long userId, Long targetUserId);

    Result<Void> unfollow(Long userId, Long targetUserId);

    Result<Boolean> checkFollow(Long userId, Long targetUserId);

    Result<Page<UserDTO>> getMyFollows(Long userId, Integer page, Integer size);
}
