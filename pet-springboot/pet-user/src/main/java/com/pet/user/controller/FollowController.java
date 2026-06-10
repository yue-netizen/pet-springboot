package com.pet.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import com.pet.user.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户关注")
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "关注用户")
    @PostMapping
    public Result<Void> follow(@RequestBody FollowRequest request,
                                @RequestHeader("X-User-Id") Long userId) {
        return followService.follow(userId, request.getTargetUserId());
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/{targetUserId}")
    public Result<Void> unfollow(@PathVariable Long targetUserId,
                                  @RequestHeader("X-User-Id") Long userId) {
        return followService.unfollow(userId, targetUserId);
    }

    @Operation(summary = "检查是否已关注")
    @GetMapping("/check")
    public Result<Boolean> checkFollow(@RequestParam Long targetUserId,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return followService.checkFollow(userId, targetUserId);
    }

    @Operation(summary = "获取我的关注列表")
    @GetMapping("/my")
    public Result<Page<UserDTO>> getMyFollows(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size,
                                               @RequestHeader("X-User-Id") Long userId) {
        return followService.getMyFollows(userId, page, size);
    }

    public static class FollowRequest {
        private Long targetUserId;

        public Long getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(Long targetUserId) {
            this.targetUserId = targetUserId;
        }
    }
}
