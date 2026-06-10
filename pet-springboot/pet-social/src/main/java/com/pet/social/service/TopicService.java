package com.pet.social.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.social.entity.Topic;

public interface TopicService extends IService<Topic> {

    Result<IPage<Topic>> getTrendingTopics(Integer page, Integer size);

    Result<IPage<Topic>> searchTopics(String keyword, Integer page, Integer size);

    Result<Topic> createTopic(String name);

    void incrementTopicUseCount(String name);
}
