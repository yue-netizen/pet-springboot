package com.pet.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 安全配置校验器
 * 
 * 功能说明：
 * - 在应用启动时检查关键安全配置是否已正确设置
 * - 如果检测到使用默认值或弱密码，会输出警告或阻止启动
 * - 帮助开发者避免因配置疏忽导致的安全问题
 * 
 * 使用场景：
 * - 开发环境：输出警告提醒开发者修改默认配置
 * - 生产环境：强制要求配置，未配置则拒绝启动
 * 
 * 校验项：
 * 1. JWT 密钥（必须配置强密钥）
 * 2. 数据库密码（不能使用默认弱密码）
 * 3. Redis 密码（生产环境必须设置）
 * 4. OSS AccessKey（如果启用OSS）
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "pet.security", name = "validation.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfigValidator implements ApplicationRunner {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.rabbitmq.password:}")
    private String rabbitmqPassword;

    @Value("${aliyun.oss.access-key-id:}")
    private String ossAccessKeyId;

    @Value("${aliyun.oss.access-key-secret:}")
    private String ossAccessKeySecret;

    /** 是否为生产环境（通过环境变量判断） */
    private boolean isProduction() {
        String profile = System.getProperty("spring.profiles.active",
                System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "dev"));
        return profile.contains("prod") || profile.contains("production");
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("🔐 开始执行安全配置校验...");
        
        int warningCount = 0;
        int errorCount = 0;

        // 1. JWT 密钥检查（最严格）
        if (!validateJwtSecret()) {
            if (isProduction()) {
                errorCount++;
                log.error("❌ [致命] JWT密钥未配置！生产环境无法启动！");
            } else {
                warningCount++;
                log.warn("⚠️  [警告] JWT密钥未配置或使用默认值！请尽快配置！");
            }
        } else {
            log.info("✅ JWT密钥：已配置");
        }

        // 2. 数据库密码检查
        if (isWeakPassword(dbPassword)) {
            if (isProduction()) {
                errorCount++;
                log.error("❌ [严重] 数据库使用了弱密码！生产环境禁止使用默认密码！");
            } else {
                warningCount++;
                log.warn("⚠️  [警告] 数据库可能使用了默认弱密码！建议修改！");
            }
        } else if (dbPassword != null && !dbPassword.isEmpty()) {
            log.info("✅ 数据库密码：已配置");
        }

        // 3. Redis 密码检查
        if (isProduction() && (redisPassword == null || redisPassword.isEmpty())) {
            errorCount++;
            log.error("❌ [严重] Redis未设置密码！生产环境必须启用认证！");
        } else if (redisPassword != null && !redisPassword.isEmpty()) {
            log.info("✅ Redis密码：已配置");
        } else {
            log.info("ℹ️  Redis密码：未设置（开发模式允许）");
        }

        // 4. RabbitMQ 密码检查
        if ("guest".equals(rabbitmqPassword) && isProduction()) {
            errorCount++;
            log.error("❌ [严重] RabbitMQ使用默认guest账号！生产环境必须修改！");
        } else if (rabbitmqPassword != null && !rabbitmqPassword.isEmpty()) {
            log.info("✅ RabbitMQ密码：已配置");
        }

        // 5. OSS 配置检查（可选）
        if ((ossAccessKeyId == null || ossAccessKeyId.isEmpty()) && 
            (ossAccessKeySecret == null || ossAccessKeySecret.isEmpty())) {
            log.info("ℹ️  OSS配置：未配置（文件上传功能将不可用）");
        } else if (ossAccessKeyId != null && !ossAccessKeyId.isEmpty()) {
            log.info("✅ OSS配置：已配置");
        }

        // 输出总结
        log.info("🔐 安全配置校验完成：{} 个错误, {} 个警告", errorCount, warningCount);
        
        if (errorCount > 0 && isProduction()) {
            log.error("");
            log.error("╔══════════════════════════════════════════════╗");
            log.error("║     ⛔ 应用无法启动！存在严重的配置问题！      ║");
            log.error("╠══════════════════════════════════════════════╣");
            log.error("║  请修复以下问题后重新启动：                   ║");
            log.error("║  1. 设置环境变量 JWT_SECRET_KEY               ║");
            log.error("║  2. 修改数据库、Redis、RabbitMQ 的密码         ║");
            log.error("║  3. 参考 .env.example 文件进行配置             ║");
            log.error("╚══════════════════════════════════════════════╝");
            log.error("");
            
            throw new IllegalStateException(
                "安全配置校验失败！请修复上述 " + errorCount + " 个错误后重启应用。\n" +
                "详细配置说明请参考 .env.example 文件"
            );
        } else if (warningCount > 0) {
            log.warn("");
            log.warn("⚠️  发现 {} 个配置警告，建议在上线前修复！", warningCount);
            log.warn("💡 提示：复制 .env.example 为 .env 并修改其中的配置\n");
        } else {
            log.info("🎉 所有安全配置检查通过！");
        }
    }

    /**
     * 验证JWT密钥是否有效
     * @return true-有效，false-无效或使用默认值
     */
    private boolean validateJwtSecret() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否是已知的示例/默认密钥
        String[] knownDefaultKeys = {
            "pet-adoption-platform-jwt-secret-key-for-token-generation-2024",
            "your-secret-key",
            "my-secret-key",
            "jwt-secret-key"
        };
        
        for (String defaultKey : knownDefaultKeys) {
            if (jwtSecret.equalsIgnoreCase(defaultKey)) {
                return false;
            }
        }
        
        // 检查长度是否足够（至少32位）
        return jwtSecret.length() >= 32;
    }

    /**
     * 检查是否为常见的弱密码
     * @param password 要检查的密码
     * @return true-是弱密码，false-不是弱密码
     */
    private boolean isWeakPassword(String password) {
        if (password == null || password.isEmpty()) {
            return true;
        }
        
        // 常见弱密码列表
        String[] weakPasswords = {
            "123456",
            "password",
            "admin",
            "root",
            "123456789",
            "12345678",
            "1234567",
            "12345",
            "1234",
            "123",
            "guest",
            "123321",
            "abc123",
            "password123",
            "admin123",
            "root123",
            "test",
            "test123",
            "qwerty",
            "letmein",
            "welcome",
            "monkey",
            "dragon",
            "master",
            "hello",
            "charlie",
            "donald",
            "login",
            "starwars",
            "passw0rd"
        };
        
        for (String weak : weakPasswords) {
            if (password.equalsIgnoreCase(weak)) {
                return true;
            }
        }
        
        // 检查是否太短（小于8位）
        if (password.length() < 8) {
            return true;
        }
        
        return false;
    }
}
