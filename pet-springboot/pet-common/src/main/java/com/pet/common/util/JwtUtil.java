package com.pet.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * 功能说明：
 * - 生成JWT Token（用于用户登录后的身份认证）
 * - 解析和验证Token
 * - 从Token中提取用户信息
 * 
 * 使用场景：
 * - 用户登录成功后生成Token
 * - API请求时验证用户身份
 * - 获取当前登录用户的信息
 * 
 * 安全提示：
 * ⚠️ JWT密钥必须从配置文件读取，禁止硬编码！
 * ⚠️ 生产环境务必修改默认密钥为强密码！
 * ⚠️ 建议使用至少32位的随机字符串作为密钥
 * 
 * 配置方式：
 * 在 application.yaml 中添加：
 * jwt:
 *   secret: your-very-long-and-secure-secret-key-here
 * 
 * 或设置环境变量：
 * export JWT_SECRET_KEY=your-secure-key
 */
@Slf4j
public class JwtUtil {

    /**
     * JWT 密钥（从系统属性或环境变量读取）
     *
     * ⚠️⚠️⚠️ 极其重要：生产环境必须配置此密钥！！！
     *
     * 优先级（按顺序）：
     * 1. JVM系统属性：-Djwt.secret=xxx
     * 2. 环境变量：JWT_SECRET_KEY=xxx
     *
     * 安全要求：
     * ✅ 必须配置，不允许为空！
     * ✅ 至少32位随机字符串（推荐64位）
     * ✅ 包含大小写字母、数字、特殊字符混合
     * ✅ 建议每90天更换一次
     *
     * ❌ 禁止行为：
     * ❌ 禁止使用默认值（已移除默认值，未配置将启动失败）
     * ❌ 禁止使用简单密码（如123456、password等）
     * ❌ 禁止在代码中硬编码密钥
     *
     * 配置示例：
     * # 方式1: 环境变量（推荐）
     * export JWT_SECRET_KEY="AbcDefGhiJklMnoPqrStuVwXyz0123456789!@#$%"
     *
     * # 方式2: application.yaml
     * jwt:
     *   secret: "your-very-long-and-secure-random-secret-key"
     *
     * # 方式3: 启动参数
     * java -Djwt.secret="your-secret-key" -jar app.jar
     *
     * 生成强密钥命令：
     * Linux/Mac: openssl rand -base64 48
     * Windows: powershell -Command "[System.Convert]::ToBase64String((1..48|%{[byte](Get-Random -Max 256)}))"
     */
    private static final String SECRET_KEY = System.getProperty("jwt.secret",
            System.getenv().getOrDefault("JWT_SECRET_KEY", ""));

    /** Token有效期：24小时（单位：毫秒） */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    /** HTTP请求头名称 */
    private static final String HEADER = "Authorization";

    /** Token前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 获取密钥的SecretKey对象
     * @return 用于签名和验证的SecretKey
     * @throws IllegalStateException JWT密钥未配置时抛出异常
     */
    private static SecretKey getSecretKey() {
        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
            throw new IllegalStateException(
                "❌ JWT 密钥未配置！系统无法启动。\n\n" +
                "请按以下方式之一配置JWT密钥：\n" +
                "1. 设置环境变量: export JWT_SECRET_KEY=\"your-very-long-secret-key\"\n" +
                "2. 在 application.yaml 中添加: jwt.secret: your-secret-key\n" +
                "3. 启动参数: -Djwt.secret=your-secret-key\n\n" +
                "生成强密钥命令:\n" +
                "  Linux/Mac: openssl rand -base64 48\n" +
                "  Windows: powershell -Command \"[System.Convert]::ToBase64String((1..48|%{[byte](Get-Random -Max 256)}))\""
            );
        }
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token字符串
     * 
     * 使用示例：
     * String token = JwtUtil.generateToken(1L, "admin");
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 创建Token（内部方法）
     * @param claims 要存入Token的声明信息
     * @param subject 主题（通常为用户名）
     * @return JWT Token字符串
     */
    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析Token并获取Claims
     * 
     * @param token JWT Token字符串
     * @return Token中的声明信息
     * @throws Exception Token无效或过期时抛出异常
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查Token是否已过期
     * @param token JWT Token
     * @return true-已过期，false-未过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取HTTP请求头名称
     * @return 请求头字段名（Authorization）
     */
    public static String getHeader() {
        return HEADER;
    }

    /**
     * 获取Token前缀
     * @return Token前缀（Bearer ）
     */
    public static String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
}
