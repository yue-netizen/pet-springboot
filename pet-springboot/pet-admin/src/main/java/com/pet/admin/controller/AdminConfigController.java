package com.pet.admin.controller;

import com.pet.admin.entity.SysConfig;
import com.pet.admin.service.SysConfigService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员系统配置管理")
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class AdminConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "获取配置列表")
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        return Result.success(sysConfigService.getAllConfigs());
    }

    @Operation(summary = "根据key获取配置")
    @GetMapping("/{key}")
    public Result<SysConfig> getByKey(@PathVariable("key") String key) {
        SysConfig config = sysConfigService.getByKey(key);
        return Result.success(config);
    }

    @Operation(summary = "更新配置")
    @PutMapping("/{key}")
    public Result<Void> update(@PathVariable("key") String key, @RequestBody SysConfig config) {
        sysConfigService.updateByKey(key, config.getConfigValue());
        return Result.success();
    }
}
