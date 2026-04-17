package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Employee;
import com.pet.admin.entity.JobApplication;
import com.pet.admin.mapper.EmployeeMapper;
import com.pet.admin.mapper.JobApplicationMapper;
import com.pet.admin.service.EmployeeService;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private final JobApplicationMapper jobApplicationMapper;

    public EmployeeServiceImpl(JobApplicationMapper jobApplicationMapper) {
        this.jobApplicationMapper = jobApplicationMapper;
    }

    @Override
    public Result<Page<JobApplication>> getApplicationList(Integer page, Integer size) {
        Page<JobApplication> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(JobApplication::getCreateTime);
        Page<JobApplication> result = jobApplicationMapper.selectPage(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Page<Employee>> getEmployeeList(Integer page, Integer size) {
        Page<Employee> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Employee::getCreateTime);
        Page<Employee> result = this.page(pageInfo, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<Employee> getEmployeeById(Long id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw BusinessException.of("员工不存在");
        }
        return Result.success(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveApplication(Long applicationId) {
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw BusinessException.of("申请不存在");
        }
        
        LambdaUpdateWrapper<JobApplication> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(JobApplication::getId, applicationId)
                .set(JobApplication::getStatus, 1);
        jobApplicationMapper.update(null, updateWrapper);
        
        Employee employee = new Employee();
        employee.setUserId(application.getUserId());
        employee.setJobId(application.getJobId());
        employee.setName(application.getName());
        employee.setPhone(application.getPhone());
        employee.setEmail(application.getEmail());
        employee.setAge(application.getAge());
        employee.setAddress(application.getAddress());
        employee.setResume(application.getResume());
        employee.setIntroduction(application.getIntroduction());
        employee.setAvailability(application.getAvailability());
        employee.setStatus(1);
        this.save(employee);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> rejectApplication(Long applicationId) {
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw BusinessException.of("申请不存在");
        }
        
        LambdaUpdateWrapper<JobApplication> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(JobApplication::getId, applicationId)
                .set(JobApplication::getStatus, 2);
        jobApplicationMapper.update(null, updateWrapper);
        
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateEmployee(Employee employee) {
        this.updateById(employee);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateEmployeeStatus(Long id, Integer status) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw BusinessException.of("员工不存在");
        }
        employee.setStatus(status);
        this.updateById(employee);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteEmployee(Long id) {
        this.removeById(id);
        return Result.success();
    }
}
