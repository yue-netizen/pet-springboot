package com.pet.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.common.result.Result;
import com.pet.chat.entity.Conversation;
import com.pet.chat.entity.Message;
import com.pet.chat.service.ChatService;
import com.pet.chat.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天管理")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    public Result<List<Conversation>> getConversations(@RequestHeader("X-User-Id") Long userId) {
        return chatService.getConversations(userId);
    }

    @Operation(summary = "获取消息列表")
    @GetMapping("/messages/{conversationId}")
    public Result<Page<Message>> getMessages(@PathVariable Long conversationId,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size) {
        return chatService.getMessages(conversationId, page, size);
    }

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<Message> sendMessage(@Valid @RequestBody MessageVO messageVO,
                                        @RequestHeader("X-User-Id") Long senderId) {
        return chatService.sendMessage(messageVO, senderId);
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read/{conversationId}")
    public Result<Void> markAsRead(@PathVariable Long conversationId,
                                    @RequestHeader("X-User-Id") Long userId) {
        return chatService.markAsRead(conversationId, userId);
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread")
    public Result<Integer> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return chatService.getUnreadCount(userId);
    }
}
