package com.pet.recruitment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.recruitment.entity.Job;
import com.pet.recruitment.entity.JobApplication;
import com.pet.recruitment.mapper.JobApplicationMapper;
import com.pet.recruitment.mapper.JobMapper;
import com.pet.recruitment.service.JobService;
import com.pet.recruitment.vo.JobApplicationVO;
import com.pet.recruitment.vo.JobQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final JobApplicationMapper jobApplicationMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Result<Page<Job>> getJobList(JobQueryVO queryVO) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(queryVO.getTitle())) {
            wrapper.like(Job::getTitle, queryVO.getTitle());
        }
        if (StrUtil.isNotBlank(queryVO.getType())) {
            wrapper.eq(Job::getType, queryVO.getType());
        }
        if (StrUtil.isNotBlank(queryVO.getLocation())) {
            wrapper.like(Job::getLocation, queryVO.getLocation());
        }
        if (queryVO.getStatus() != null) {
            wrapper.eq(Job::getStatus, queryVO.getStatus());
        } else {
            wrapper.eq(Job::getStatus, 1);
        }
        
        wrapper.orderByDesc(Job::getCreateTime);
        
        Page<Job> page = new Page<>(queryVO.getPage(), queryVO.getSize());
        Page<Job> result = this.page(page, wrapper);
        
        return Result.success(result);
    }

    @Override
    public Result<Job> getJobById(Long id) {
        Job job = this.getById(id);
        if (job == null) {
            throw BusinessException.of("职位不存在");
        }
        return Result.success(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createJob(Job job) {
        job.setStatus(1);
        this.save(job);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateJob(Job job) {
        Job existingJob = this.getById(job.getId());
        if (existingJob == null) {
            throw BusinessException.of("职位不存在");
        }
        this.updateById(job);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteJob(Long id) {
        this.removeById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> applyJob(JobApplicationVO applicationVO, Long userId) {
        Job job = this.getById(applicationVO.getJobId());
        if (job == null) {
            throw BusinessException.of("职位不存在");
        }
        
        if (job.getStatus() != 1) {
            throw BusinessException.of("该职位已关闭招聘");
        }
        
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getJobId, applicationVO.getJobId())
                .eq(JobApplication::getUserId, userId);
        if (jobApplicationMapper.selectCount(wrapper) > 0) {
            throw BusinessException.of("您已申请过该职位");
        }
        
        JobApplication application = new JobApplication();
        application.setJobId(applicationVO.getJobId());
        application.setUserId(userId);
        application.setName(applicationVO.getName());
        application.setPhone(applicationVO.getPhone());
        application.setEmail(applicationVO.getEmail());
        application.setResume(applicationVO.getResume());
        application.setIntroduction(applicationVO.getIntroduction());
        application.setStatus(0);
        
        jobApplicationMapper.insert(application);
        
        rabbitTemplate.convertAndSend("recruitment.exchange", "job.applied", application);
        
        return Result.success();
    }

    @Override
    public Result<Page<JobApplication>> getMyApplications(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getUserId, userId)
                .orderByDesc(JobApplication::getCreateTime);
        
        Page<JobApplication> applicationPage = new Page<>(page, size);
        Page<JobApplication> result = jobApplicationMapper.selectPage(applicationPage, wrapper);
        
        return Result.success(result);
    }
}
