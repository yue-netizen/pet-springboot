package com.pet.recruitment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.recruitment.entity.Job;
import com.pet.recruitment.entity.JobApplication;
import com.pet.recruitment.service.JobService;
import com.pet.recruitment.vo.JobApplicationVO;
import com.pet.recruitment.vo.JobQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "招聘管理")
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "获取职位列表")
    @GetMapping("/list")
    public Result<Page<Job>> getJobList(JobQueryVO queryVO) {
        return jobService.getJobList(queryVO);
    }

    @Operation(summary = "获取职位详情")
    @GetMapping("/{id}")
    public Result<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @Operation(summary = "创建职位")
    @PostMapping
    public Result<Void> createJob(@RequestBody Job job) {
        return jobService.createJob(job);
    }

    @Operation(summary = "更新职位")
    @PutMapping
    public Result<Void> updateJob(@RequestBody Job job) {
        return jobService.updateJob(job);
    }

    @Operation(summary = "删除职位")
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        return jobService.deleteJob(id);
    }

    @Operation(summary = "申请职位")
    @PostMapping("/apply")
    public Result<Void> applyJob(@Valid @RequestBody JobApplicationVO applicationVO,
                                  @RequestHeader("X-User-Id") Long userId) {
        return jobService.applyJob(applicationVO, userId);
    }

    @Operation(summary = "获取我的申请记录")
    @GetMapping("/my-applications")
    public Result<Page<JobApplication>> getMyApplications(@RequestParam(defaultValue = "1") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer size,
                                                           @RequestHeader("X-User-Id") Long userId) {
        return jobService.getMyApplications(userId, page, size);
    }
}
