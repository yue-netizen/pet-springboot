package com.pet.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.admin.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
