package com.pet.tips;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pet.common.feign")
@MapperScan({"com.pet.tips.mapper", "com.pet.tips.ai.mapper"})
@ComponentScan(basePackages = {"com.pet.tips", "com.pet.common"})
public class TipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TipsApplication.class, args);
    }
}
