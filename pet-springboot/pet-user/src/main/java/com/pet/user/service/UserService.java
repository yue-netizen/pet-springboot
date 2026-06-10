package com.pet.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import com.pet.user.entity.User;
import com.pet.user.vo.LoginVO;
import com.pet.user.vo.RegisterVO;

public interface UserService extends IService<User> {

    Result<LoginVO> login(String username, String password);

    Result<Void> register(RegisterVO registerVO);

    Result<UserDTO> getUserById(Long id);

    Result<UserDTO> getUserByUsername(String username);

    Result<UserDTO> updateUser(UserDTO userDTO);

    Result<Void> updatePassword(Long userId, String oldPassword, String newPassword);
}
