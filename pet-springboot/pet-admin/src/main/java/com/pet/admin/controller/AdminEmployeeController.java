package com.pet.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Employee;
import com.pet.admin.entity.JobApplication;
import com.pet.admin.service.EmployeeService;
import com.pet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员员工管理", description = "管理员员工管理接口")
@RestController
@RequestMapping("/admin/employee")
public class AdminEmployeeController {

    @Resource
    private EmployeeService employeeService;

    @Operation(summary = "获取求职申请列表")
    @GetMapping("/applications")
    public Result<Page<JobApplication>> getApplicationList(@RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        return employeeService.getApplicationList(page, size);
    }

    @Operation(summary = "获取员工列表")
    @GetMapping("/list")
    public Result<Page<Employee>> getEmployeeList(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return employeeService.getEmployeeList(page, size);
    }

    @Operation(summary = "获取员工详情")
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @Operation(summary = "同意求职申请")
    @PutMapping("/approve/{applicationId}")
    public Result<Void> approveApplication(@PathVariable Long applicationId) {
        return employeeService.approveApplication(applicationId);
    }

    @Operation(summary = "拒绝求职申请")
    @PutMapping("/reject/{applicationId}")
    public Result<Void> rejectApplication(@PathVariable Long applicationId) {
        return employeeService.rejectApplication(applicationId);
    }

    @Operation(summary = "更新员工信息")
    @PutMapping
    public Result<Void> updateEmployee(@RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @Operation(summary = "更新员工状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateEmployeeStatus(@PathVariable Long id, @RequestParam Integer status) {
        return employeeService.updateEmployeeStatus(id, status);
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }
}
