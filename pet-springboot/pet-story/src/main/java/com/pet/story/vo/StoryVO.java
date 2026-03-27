package com.pet.story.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long petId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String image;
}
