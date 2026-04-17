package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.Job;
import com.pet.common.result.Result;

public interface JobService extends IService<Job> {

    Result<Page<Job>> getJobList(Integer page, Integer size);

    Result<Job> getJobById(Long id);

    Result<Void> addJob(Job job);

    Result<Void> updateJob(Job job);

    Result<Void> deleteJob(Long id);
}
