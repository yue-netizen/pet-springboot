package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.admin.entity.Post;
import com.pet.common.result.Result;

public interface PostService {

    Result<Page<Post>> getPostList(Integer page, Integer size);

    Result<Post> getPostById(Long id);

    Result<Void> deletePost(Long id);
}
