package com.pet.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.constant.RedisConstants;
import com.pet.common.dto.UserDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.common.util.JwtUtil;
import com.pet.user.entity.User;
import com.pet.user.mapper.UserMapper;
import com.pet.user.service.UserService;
import com.pet.user.vo.LoginVO;
import com.pet.user.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<LoginVO> login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        if (user.getStatus() != 1) {
            throw BusinessException.of("账号已被禁用");
        }
        
        String encryptedPassword = DigestUtil.md5Hex(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw BusinessException.of("密码错误");
        }
        
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        
        redisTemplate.opsForValue().set(
                RedisConstants.USER_TOKEN_KEY + user.getId(),
                token,
                RedisConstants.TOKEN_EXPIRE_TIME,
                TimeUnit.SECONDS
        );
        
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setToken(token);
        loginVO.setRole(user.getRole());
        
        return Result.success("登录成功", loginVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> register(RegisterVO registerVO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerVO.getUsername());
        long count = this.count(wrapper);
        if (count > 0) {
            throw BusinessException.of("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(registerVO.getUsername());
        user.setPassword(DigestUtil.md5Hex(registerVO.getPassword()));
        user.setNickname(registerVO.getNickname());
        user.setEmail(registerVO.getEmail());
        user.setPhone(registerVO.getPhone());
        user.setStatus(1);
        user.setRole(0);
        
        this.save(user);
        
        return Result.success();
    }

    @Override
    public Result<UserDTO> getUserById(Long id) {
        String cacheKey = RedisConstants.USER_INFO_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return Result.success((UserDTO) cached);
        }
        
        User user = this.getById(id);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        
        redisTemplate.opsForValue().set(
                cacheKey,
                userDTO,
                RedisConstants.CACHE_EXPIRE_TIME,
                TimeUnit.SECONDS
        );
        
        return Result.success(userDTO);
    }

    @Override
    public Result<UserDTO> getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        return Result.success(userDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<UserDTO> updateUser(UserDTO userDTO) {
        User user = this.getById(userDTO.getId());
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        user.setNickname(userDTO.getNickname());
        user.setAvatar(userDTO.getAvatar());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        
        this.updateById(user);
        
        String cacheKey = RedisConstants.USER_INFO_KEY + user.getId();
        redisTemplate.delete(cacheKey);
        
        return Result.success(userDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw BusinessException.of("用户不存在");
        }
        
        String encryptedOldPassword = DigestUtil.md5Hex(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw BusinessException.of("原密码错误");
        }
        
        user.setPassword(DigestUtil.md5Hex(newPassword));
        this.updateById(user);
        
        redisTemplate.delete(RedisConstants.USER_TOKEN_KEY + userId);
        
        return Result.success();
    }
}
