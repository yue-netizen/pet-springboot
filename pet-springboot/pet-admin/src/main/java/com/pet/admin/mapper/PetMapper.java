package com.pet.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.admin.entity.Pet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
