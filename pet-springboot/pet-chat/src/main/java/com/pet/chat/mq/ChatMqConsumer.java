package com.pet.chat.mq;

import com.pet.chat.config.ChatMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMqConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = ChatMqConfig.MESSAGE_QUEUE)
    public void consumeMessage(@Payload Map<String, Object> message,
                                Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("收到聊天消息MQ, senderId={}, receiverId={}, content={}",
                    message.get("senderId"), message.get("receiverId"), message.get("content"));

            Long receiverId = ((Number) message.get("receiverId")).longValue();
            Long conversationId = ((Number) message.get("conversationId")).longValue();

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(receiverId),
                    "/queue/chat",
                    message
            );

            messagingTemplate.convertAndSend(
                    "/topic/chat/conversation/" + conversationId,
                    message
            );

            log.info("消息已通过WebSocket推送给用户, receiverId={}", receiverId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理聊天MQ消息失败", e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("NACK消息失败", ex);
            }
        }
    }
}
