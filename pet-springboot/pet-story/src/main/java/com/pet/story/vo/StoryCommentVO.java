package com.pet.story.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoryCommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "故事ID不能为空")
    private Long storyId;

    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
