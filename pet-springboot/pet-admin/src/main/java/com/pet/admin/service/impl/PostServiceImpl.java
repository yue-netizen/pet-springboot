package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.Post;
import com.pet.admin.mapper.PostMapper;
import com.pet.admin.service.PostService;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public Result<Page<Post>> getPostList(Integer page, Integer size) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Post::getCreateTime);

        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result = this.page(postPage, wrapper);

        return Result.success(result);
    }

    @Override
    public Result<Post> getPostById(Long id) {
        Post post = this.getById(id);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }
        return Result.success(post);
    }

    @Override
    public Result<Void> deletePost(Long id) {
        Post post = this.getById(id);
        if (post == null) {
            throw BusinessException.of("帖子不存在");
        }

        this.removeById(id);
        return Result.success();
    }
}
