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

@Slf4j
public class JwtUtil {

    private static final String SECRET_KEY;
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;
    private static final String HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    static {
        // 从环境变量读取JWT密钥，如果未配置则抛出异常
        String key = System.getenv("JWT_SECRET_KEY");
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalStateException("JWT密钥未配置！请设置环境变量 JWT_SECRET_KEY");
        }
        SECRET_KEY = key.trim();
        log.info("JWT密钥已成功加载");
    }

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static String getHeader() {
        return HEADER;
    }

    public static String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
}
