package com.pet.social;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pet.common.feign")
@EnableRabbit
@MapperScan("com.pet.social.mapper")
@ComponentScan(basePackages = {"com.pet.social", "com.pet.common"})
public class SocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
}
