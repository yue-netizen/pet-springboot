package com.pet.gateway.filter;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器
 * 
 * 功能说明：
 * - 拦截所有API请求，验证JWT Token
 * - 白名单路径无需认证即可访问
 * - 验证通过后，将用户信息注入到请求头中传递给下游服务
 * 
 * 安全提示：
 * - JWT密钥必须从环境变量读取，禁止硬编码！
 * - 生产环境请使用强密码（至少32位随机字符串）
 * - 建议定期更换JWT密钥
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * JWT 密钥（从配置文件或环境变量读取）
     *
     * ⚠️⚠️⚠️ 极其重要：生产环境必须修改此密钥！！！
     *
     * 配置方式（按优先级排序）：
     * 1. 环境变量：JWT_SECRET_KEY=your-very-long-random-secret-key-here
     * 2. application.yaml 配置：jwt.secret: your-secret-key
     * 3. 启动参数：-Djwt.secret=your-secret-key
     *
     * 安全要求：
     * ✅ 必须使用至少 32 位随机字符串
     * ✅ 包含大小写字母、数字、特殊字符
     * ✅ 定期更换（建议每90天）
     * ❌ 禁止使用示例中的默认值！
     *
     * 生成强密钥的方法：
     * Linux/Mac: openssl rand -base64 48
     * Windows: powershell -Command "[System.Convert]::ToBase64String((1..32|%{[byte](Get-Random -Max 256)}))"
     */
    @Value("${jwt.secret:}")
    private String secretKey;

    /** Token前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 白名单路径（无需认证即可访问的接口）
     *
     * ⚠️ 安全提示：
     * - 使用 startsWith 精确匹配前缀，避免 contains 导致的安全绕过
     * - 路径必须以 / 开头
     * - 添加新路径时注意不要过于宽泛
     *
     * 包含：
     * - 认证相关：登录、注册
     * - 公开查询：宠物列表、故事列表等
     * - 配置接口：公开的系统配置
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/user/register",
            "/config/",
            "/pet/list",
            "/pet/story",
            "/favorite/check/",
            "/story/list",
            "/story/comment",
            "/recruitment/list",
            "/job/list",
            "/job/apply",
            "/tips/list",
            "/topic/trending",
            "/topic/search",
            "/post/list",
            "/post/search",
            "/post/detail",
            "/tips-ai/ask"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            return unauthorized(exchange, "未登录或登录已过期");
        }

        if (!token.startsWith(TOKEN_PREFIX)) {
            return unauthorized(exchange, "Token格式错误");
        }

        token = token.substring(TOKEN_PREFIX.length());

        try {
            validateSecretKey();
            Claims claims = parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Username", username)
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return unauthorized(exchange, "Token验证失败");
        }
    }

    /**
     * 验证JWT密钥是否已配置
     * @throws IllegalStateException 密钥未配置时抛出异常
     */
    private void validateSecretKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "JWT密钥未配置！请在application.yaml或环境变量中设置jwt.secret"
            );
        }
    }

    /**
     * 检查是否为白名单路径（使用精确前缀匹配）
     *
     * ✅ 安全改进：
     * - 使用 startsWith 替代 contains，避免路径遍历攻击
     * - 更严格的匹配逻辑
     *
     * @param path 请求路径
     * @return 是否在白名单中
     */
    private boolean isWhitePath(String path) {
        for (String whitePath : WHITE_LIST) {
            if (path.equals(whitePath) || path.startsWith(whitePath + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析并验证JWT Token
     * @param token JWT Token字符串
     * @return Token中的Claims信息
     * @throws Exception Token无效时抛出异常
     */
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 返回未授权响应
     * @param exchange HTTP交换对象
     * @param message 错误消息
     * @return 未授权的Mono响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + message + "\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
