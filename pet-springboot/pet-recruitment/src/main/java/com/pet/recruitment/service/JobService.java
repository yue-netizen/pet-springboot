package com.pet.recruitment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.recruitment.entity.Job;
import com.pet.recruitment.entity.JobApplication;
import com.pet.recruitment.vo.JobApplicationVO;
import com.pet.recruitment.vo.JobQueryVO;

public interface JobService extends IService<Job> {

    Result<Page<Job>> getJobList(JobQueryVO queryVO);

    Result<Job> getJobById(Long id);

    Result<Void> createJob(Job job);

    Result<Void> updateJob(Job job);

    Result<Void> deleteJob(Long id);

    Result<Void> applyJob(JobApplicationVO applicationVO, Long userId);

    Result<Page<JobApplication>> getMyApplications(Long userId, Integer page, Integer size);
}
