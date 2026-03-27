package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.User;
import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;

import java.util.List;

public interface UserService extends IService<User> {

    Result<List<UserDTO>> getAllUsers();

    Result<UserDTO> updateUserByAdmin(UserDTO userDTO);

    Result<Void> deleteUser(Long id);

    Result<Void> updateUserStatus(Long id, Integer status);
}
