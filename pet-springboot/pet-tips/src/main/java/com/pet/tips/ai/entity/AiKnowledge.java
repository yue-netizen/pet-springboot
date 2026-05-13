package com.pet.tips.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pet.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_knowledge")
public class AiKnowledge extends BaseEntity {

    private String title;

    private String category;

    private String content;

    private String source;

    private Integer status;
}
