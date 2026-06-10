package com.pet.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OssConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true")
    public OSS ossClient() {
        if (!StringUtils.hasText(accessKeyId) || !StringUtils.hasText(accessKeySecret)) {
            throw new IllegalArgumentException("OSS accessKeyId and accessKeySecret must not be empty");
        }
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
