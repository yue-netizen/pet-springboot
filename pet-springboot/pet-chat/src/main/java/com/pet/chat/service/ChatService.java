package com.pet.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.common.result.Result;
import com.pet.chat.entity.Conversation;
import com.pet.chat.entity.Message;
import com.pet.chat.vo.MessageVO;

import java.util.List;

public interface ChatService extends IService<Conversation> {

    Result<List<Conversation>> getConversations(Long userId);

    Result<Page<Message>> getMessages(Long conversationId, Integer page, Integer size);

    Result<Message> sendMessage(MessageVO messageVO, Long senderId);

    Result<Void> markAsRead(Long conversationId, Long userId);

    Result<Integer> getUnreadCount(Long userId);
}
