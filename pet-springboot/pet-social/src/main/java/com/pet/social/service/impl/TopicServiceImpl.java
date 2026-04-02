package com.pet.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.result.Result;
import com.pet.social.entity.Topic;
import com.pet.social.mapper.TopicMapper;
import com.pet.social.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Override
    public Result<IPage<Topic>> getTrendingTopics(Integer page, Integer size) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Topic::getUseCount);
        
        Page<Topic> topicPage = new Page<>(page, size);
        IPage<Topic> result = this.page(topicPage, wrapper);
        
        return Result.success(result);
    }

    @Override
    public Result<IPage<Topic>> searchTopics(String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Topic::getName, keyword)
                .orderByDesc(Topic::getUseCount);
        
        Page<Topic> topicPage = new Page<>(page, size);
        IPage<Topic> result = this.page(topicPage, wrapper);
        
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Topic> createTopic(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Result.error("话题名称不能为空");
        }
        
        String cleanName = name.trim();
        
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getName, cleanName);
        Topic existingTopic = this.getOne(wrapper);
        
        if (existingTopic != null) {
            return Result.success(existingTopic);
        }
        
        Topic topic = new Topic();
        topic.setName(cleanName);
        topic.setUseCount(0);
        this.save(topic);
        
        return Result.success(topic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementTopicUseCount(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        
        String cleanName = name.trim();
        
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getName, cleanName);
        Topic topic = this.getOne(wrapper);
        
        if (topic != null) {
            int currentCount = topic.getUseCount() != null ? topic.getUseCount() : 0;
            topic.setUseCount(currentCount + 1);
            this.updateById(topic);
        }
    }
}
