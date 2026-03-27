package com.pet.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.constant.RedisConstants;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.chat.entity.Conversation;
import com.pet.chat.entity.Message;
import com.pet.chat.mapper.ConversationMapper;
import com.pet.chat.mapper.MessageMapper;
import com.pet.chat.service.ChatService;
import com.pet.chat.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ChatService {

    private final MessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Result<List<Conversation>> getConversations(Long userId) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Conversation::getUser1Id, userId)
                        .or()
                        .eq(Conversation::getUser2Id, userId))
                .orderByDesc(Conversation::getUpdateTime);
        
        List<Conversation> conversations = this.list(wrapper);
        return Result.success(conversations);
    }

    @Override
    public Result<Page<Message>> getMessages(Long conversationId, Integer page, Integer size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId)
                .orderByAsc(Message::getCreateTime);
        
        Page<Message> messagePage = new Page<>(page, size);
        Page<Message> result = messageMapper.selectPage(messagePage, wrapper);
        
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Message> sendMessage(MessageVO messageVO, Long senderId) {
        Long receiverId = messageVO.getReceiverId();
        
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.and(w1 -> w1.eq(Conversation::getUser1Id, senderId)
                                        .eq(Conversation::getUser2Id, receiverId))
                        .or(w2 -> w2.eq(Conversation::getUser1Id, receiverId)
                                        .eq(Conversation::getUser2Id, senderId)));
        
        Conversation conversation = this.getOne(wrapper);
        
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser1Id(senderId);
            conversation.setUser2Id(receiverId);
            conversation.setUnreadCount1(0);
            conversation.setUnreadCount2(0);
            conversation.setStatus(1);
            this.save(conversation);
        }
        
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(messageVO.getContent());
        message.setType(messageVO.getType());
        message.setStatus(0);
        messageMapper.insert(message);
        
        conversation.setLastMessage(messageVO.getContent());
        if (conversation.getUser1Id().equals(senderId)) {
            conversation.setUnreadCount2(conversation.getUnreadCount2() + 1);
        } else {
            conversation.setUnreadCount1(conversation.getUnreadCount1() + 1);
        }
        this.updateById(conversation);
        
        String unreadKey = RedisConstants.CHAT_UNREAD_KEY + receiverId;
        redisTemplate.opsForValue().increment(unreadKey);
        
        rabbitTemplate.convertAndSend("chat.exchange", "chat.message", message);
        
        return Result.success(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> markAsRead(Long conversationId, Long userId) {
        Conversation conversation = this.getById(conversationId);
        if (conversation == null) {
            throw BusinessException.of("会话不存在");
        }
        
        if (conversation.getUser1Id().equals(userId)) {
            conversation.setUnreadCount1(0);
        } else if (conversation.getUser2Id().equals(userId)) {
            conversation.setUnreadCount2(0);
        }
        this.updateById(conversation);
        
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getConversationId, conversationId)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getStatus, 0)
                .set(Message::getStatus, 1)
                .set(Message::getReadTime, LocalDateTime.now());
        messageMapper.update(null, updateWrapper);
        
        return Result.success();
    }

    @Override
    public Result<Integer> getUnreadCount(Long userId) {
        String unreadKey = RedisConstants.CHAT_UNREAD_KEY + userId;
        Object count = redisTemplate.opsForValue().get(unreadKey);
        return Result.success(count != null ? Integer.parseInt(count.toString()) : 0);
    }
}
