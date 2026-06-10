package com.pet.story;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pet.common.feign")
@MapperScan("com.pet.story.mapper")
@ComponentScan(basePackages = {"com.pet.story", "com.pet.common"})
public class StoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoryApplication.class, args);
    }
}
