package com.pet.story.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class StoryDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long petId;
    private String title;
    private String content;
    private String image;
    private Integer likeCount;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String petName;
    private String petImage;
    private String petBreed;
    private String username;
    private String userAvatar;
    private Boolean isLiked;
}
