package com.pet.common.feign;

import com.pet.common.constant.ServiceConstants;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = ServiceConstants.PET_SERVICE)
public interface PetFeignClient {

    @GetMapping("/pet/{id}")
    Result<PetDTO> getPetById(@PathVariable("id") Long id);
}
