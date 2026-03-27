package com.pet.user.controller;

import com.pet.common.result.Result;
import com.pet.user.entity.SysConfig;
import com.pet.user.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统配置管理")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "获取配置列表")
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        return Result.success(sysConfigService.list());
    }

    @Operation(summary = "根据key获取配置")
    @GetMapping("/{key}")
    public Result<SysConfig> getByKey(@PathVariable String key) {
        SysConfig config = sysConfigService.getByKey(key);
        return Result.success(config);
    }

    @Operation(summary = "更新配置")
    @PutMapping("/{key}")
    public Result<Void> update(@PathVariable String key, @RequestBody SysConfig config) {
        sysConfigService.updateByKey(key, config.getConfigValue());
        return Result.success();
    }
}
