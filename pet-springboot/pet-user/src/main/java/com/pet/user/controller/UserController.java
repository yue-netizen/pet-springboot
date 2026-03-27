package com.pet.user.controller;

import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import com.pet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "根据用户名获取用户信息")
    @GetMapping("/username/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping
    public Result<UserDTO> updateUser(@RequestBody UserDTO userDTO, 
                                       @RequestHeader("X-User-Id") Long userId) {
        userDTO.setId(userId);
        return userService.updateUser(userDTO);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword,
                                        @RequestParam String newPassword,
                                        @RequestHeader("X-User-Id") Long userId) {
        return userService.updatePassword(userId, oldPassword, newPassword);
    }
}
