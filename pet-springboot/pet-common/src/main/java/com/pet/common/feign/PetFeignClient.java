package com.pet.common.feign;

import com.pet.common.constant.ServiceConstants;
import com.pet.common.dto.PetDTO;
import com.pet.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = ServiceConstants.PET_SERVICE)
public interface PetFeignClient {

    @GetMapping("/pet/{id}")
    Result<PetDTO> getPetById(@PathVariable("id") Long id);

    @GetMapping("/pet/list")
    Result<?> getPetList(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "breed", required = false) String breed,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    );
}
