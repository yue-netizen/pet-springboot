package com.pet.social.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.social.entity.Post;
import com.pet.social.vo.PostVO;

public interface PostService extends IService<Post> {

    Result<Page<Post>> getPostList(Integer page, Integer size);

    Result<Post> getPostById(Long id);

    Result<Void> createPost(PostVO postVO, Long userId);

    Result<Void> deletePost(Long id, Long userId);

    Result<Void> likePost(Long postId, Long userId);

    Result<Void> unlikePost(Long postId, Long userId);
}
