package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Job;
import com.pet.admin.mapper.JobMapper;
import com.pet.admin.service.JobService;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Override
    public Result<Page<Job>> getJobList(Integer page, Integer size) {
        Page<Job> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Job::getCreateTime);
        Page<Job> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Job> getJobById(Long id) {
        Job job = this.getById(id);
        if (job == null) {
            throw BusinessException.of("岗位不存在");
        }
        return Result.success(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addJob(Job job) {
        if (job.getStatus() == null) {
            job.setStatus(1);
        }
        this.save(job);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateJob(Job job) {
        this.updateById(job);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteJob(Long id) {
        this.removeById(id);
        return Result.success();
    }
}
