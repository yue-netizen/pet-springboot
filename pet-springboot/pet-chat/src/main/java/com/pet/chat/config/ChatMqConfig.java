package com.pet.chat.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMqConfig {

    public static final String CHAT_EXCHANGE = "chat.exchange";
    public static final String MESSAGE_QUEUE = "chat.message.queue";
    public static final String MESSAGE_ROUTING_KEY = "chat.message";

    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange(CHAT_EXCHANGE, true, false);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(MESSAGE_QUEUE, true);
    }

    @Bean
    public Binding messageBinding() {
        return BindingBuilder.bind(messageQueue())
                .to(chatExchange())
                .with(MESSAGE_ROUTING_KEY);
    }
}
