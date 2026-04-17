package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Job;
import com.pet.admin.service.JobService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员岗位管理", description = "管理员岗位管理接口")
@RestController
@RequestMapping("/admin/job")
public class AdminJobController {

    @Resource
    private JobService jobService;

    @Operation(summary = "获取岗位列表")
    @GetMapping("/list")
    public Result<Page<Job>> getJobList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return jobService.getJobList(page, size);
    }

    @Operation(summary = "获取岗位详情")
    @GetMapping("/{id}")
    public Result<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @Operation(summary = "添加岗位")
    @PostMapping
    public Result<Void> addJob(@RequestBody Job job) {
        return jobService.addJob(job);
    }

    @Operation(summary = "更新岗位")
    @PutMapping
    public Result<Void> updateJob(@RequestBody Job job) {
        return jobService.updateJob(job);
    }

    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        return jobService.deleteJob(id);
    }
}
