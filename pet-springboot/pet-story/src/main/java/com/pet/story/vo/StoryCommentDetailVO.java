package com.pet.story.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class StoryCommentDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long storyId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String userAvatar;
}
