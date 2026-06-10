package com.pet.tips.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.tips.ai.entity.AiKnowledge;

public interface AiKnowledgeService extends IService<AiKnowledge> {

    Result<IPage<AiKnowledge>> getList(Integer page, Integer size, String category, String keyword);

    Result<AiKnowledge> getById(Long id);

    Result<Void> create(AiKnowledge knowledge);

    Result<Void> update(Long id, AiKnowledge knowledge);

    Result<Void> delete(Long id);

    Result<java.util.List<AiKnowledge>> listEnabled();
}
