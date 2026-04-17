package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.Employee;
import com.pet.admin.entity.JobApplication;
import com.pet.common.result.Result;

public interface EmployeeService extends IService<Employee> {

    Result<Page<JobApplication>> getApplicationList(Integer page, Integer size);

    Result<Page<Employee>> getEmployeeList(Integer page, Integer size);

    Result<Employee> getEmployeeById(Long id);

    Result<Void> approveApplication(Long applicationId);

    Result<Void> rejectApplication(Long applicationId);

    Result<Void> updateEmployee(Employee employee);

    Result<Void> updateEmployeeStatus(Long id, Integer status);

    Result<Void> deleteEmployee(Long id);
}
