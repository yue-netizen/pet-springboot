package com.pet.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.dto.UserDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.user.entity.Follow;
import com.pet.user.mapper.FollowMapper;
import com.pet.user.service.FollowService;
import com.pet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    private final FollowMapper followMapper;
    private final UserService userService;

    @Override
    public Result<Void> follow(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw BusinessException.of("不能关注自己");
        }

        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getTargetUserId, targetUserId);

        if (followMapper.selectCount(wrapper) > 0) {
            throw BusinessException.of("已经关注过了");
        }

        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setTargetUserId(targetUserId);
        followMapper.insert(follow);

        return Result.success();
    }

    @Override
    public Result<Void> unfollow(Long userId, Long targetUserId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getTargetUserId, targetUserId);

        followMapper.delete(wrapper);
        return Result.success();
    }

    @Override
    public Result<Boolean> checkFollow(Long userId, Long targetUserId) {
        if (userId == null || targetUserId == null) {
            return Result.success(false);
        }

        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .eq(Follow::getTargetUserId, targetUserId);

        long count = followMapper.selectCount(wrapper);
        return Result.success(count > 0);
    }

    @Override
    public Result<Page<UserDTO>> getMyFollows(Long userId, Integer page, Integer size) {
        Page<Follow> followPage = new Page<>(page, size);
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId)
               .orderByDesc(Follow::getCreateTime);

        Page<Follow> result = followMapper.selectPage(followPage, wrapper);

        Page<UserDTO> userPage = new Page<>(page, size, result.getTotal());
        java.util.List<UserDTO> users = new java.util.ArrayList<>();

        for (Follow follow : result.getRecords()) {
            try {
                Result<UserDTO> userResult = userService.getUserById(follow.getTargetUserId());
                if (userResult != null && userResult.getData() != null) {
                    users.add(userResult.getData());
                }
            } catch (Exception e) {
                // skip failed user fetch
            }
        }

        userPage.setRecords(users);
        return Result.success(userPage);
    }
}
