package com.pet.admin.controller;

import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import com.pet.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员用户管理")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "获取所有用户列表")
    @GetMapping("/list")
    public Result<List<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "管理员更新用户信息")
    @PutMapping
    public Result<UserDTO> updateUserByAdmin(@RequestBody UserDTO userDTO) {
        return userService.updateUserByAdmin(userDTO);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        return userService.updateUserStatus(id, status);
    }
}
