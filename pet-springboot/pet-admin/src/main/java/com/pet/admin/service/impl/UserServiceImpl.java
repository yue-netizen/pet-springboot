package com.pet.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.User;
import com.pet.admin.mapper.UserMapper;
import com.pet.admin.service.UserService;
import com.pet.common.dto.UserDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result<List<UserDTO>> getAllUsers() {
        List<User> users = this.list();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.success(userDTOs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<UserDTO> updateUserByAdmin(UserDTO userDTO) {
        User user = this.getById(userDTO.getId());
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        
        this.updateById(user);
        
        return Result.success(BeanUtil.copyProperties(user, UserDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        if (user.getRole() == 1) {
            throw BusinessException.of("不能删除管理员账号");
        }
        this.removeById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUserStatus(Long id, Integer status) {
        User user = this.getById(id);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
        return Result.success();
    }
}
