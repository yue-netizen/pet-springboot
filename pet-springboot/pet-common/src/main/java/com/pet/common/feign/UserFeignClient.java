package com.pet.common.feign;

import com.pet.common.constant.ServiceConstants;
import com.pet.common.dto.UserDTO;
import com.pet.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = ServiceConstants.USER_SERVICE)
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    Result<UserDTO> getUserById(@PathVariable("id") Long id);

    @GetMapping("/user/username/{username}")
    Result<UserDTO> getUserByUsername(@PathVariable("username") String username);
}
