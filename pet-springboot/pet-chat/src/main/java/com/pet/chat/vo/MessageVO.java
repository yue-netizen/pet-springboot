package com.pet.chat.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private Integer type = 1;
}
