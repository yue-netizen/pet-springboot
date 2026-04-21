package com.pet.social.mq;

import com.rabbitmq.client.Channel;
import com.pet.social.config.SocialMqConfig;
import com.pet.social.service.impl.PostServiceImpl;
import com.pet.social.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMqConsumer {

    private final PostServiceImpl postServiceImpl;

    @RabbitListener(queues = SocialMqConfig.POST_CREATE_QUEUE)
    public void consumePostCreate(@Payload Map<String, Object> message,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("收到发帖MQ消息: {}", message);

            Map<String, Object> postVOMap = (Map<String, Object>) message.get("postVO");
            Number userIdNum = (Number) message.get("userId");

            PostVO postVO = new PostVO();
            if (postVOMap.get("title") != null) postVO.setTitle(postVOMap.get("title").toString());
            if (postVOMap.get("content") != null) postVO.setContent(postVOMap.get("content").toString());
            if (postVOMap.get("image") != null) postVO.setImage(postVOMap.get("image").toString());
            if (postVOMap.get("images") != null) postVO.setImages(postVOMap.get("images").toString());
            if (postVOMap.get("video") != null) postVO.setVideo(postVOMap.get("video").toString());
            if (postVOMap.get("videos") != null) postVO.setVideos(postVOMap.get("videos").toString());
            if (postVOMap.get("tags") != null) postVO.setTags(postVOMap.get("tags").toString());

            Long userId = userIdNum != null ? userIdNum.longValue() : null;
            postServiceImpl.handleCreatePost(postVO, userId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理发帖MQ消息失败", e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("NACK消息失败", ex);
            }
        }
    }
}
