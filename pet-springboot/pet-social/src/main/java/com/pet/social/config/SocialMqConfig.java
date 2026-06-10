package com.pet.social.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialMqConfig {

    public static final String SOCIAL_EXCHANGE = "social.exchange";
    public static final String POST_CREATE_QUEUE = "social.post.create.queue";
    public static final String POST_CREATE_ROUTING_KEY = "social.post.create";

    @Bean
    public DirectExchange socialExchange() {
        return new DirectExchange(SOCIAL_EXCHANGE, true, false);
    }

    @Bean
    public Queue postCreateQueue() {
        return new Queue(POST_CREATE_QUEUE, true);
    }

    @Bean
    public Binding postCreateBinding() {
        return BindingBuilder.bind(postCreateQueue())
                .to(socialExchange())
                .with(POST_CREATE_ROUTING_KEY);
    }
}
