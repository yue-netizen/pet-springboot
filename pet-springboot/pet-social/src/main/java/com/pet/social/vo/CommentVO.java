package com.pet.social.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
