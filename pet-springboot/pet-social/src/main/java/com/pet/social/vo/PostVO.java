package com.pet.social.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String image;

    private String video;
}
