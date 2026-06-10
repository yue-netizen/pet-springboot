package com.pet.user.controller;

import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import com.pet.user.service.UserService;
import com.pet.user.vo.LoginVO;
import com.pet.user.vo.RegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userService.login(username, password);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterVO registerVO) {
        return userService.register(registerVO);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("X-User-Id") Long userId) {
        return Result.success();
    }
}
