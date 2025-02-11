package com.xhs.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    // 手动创建 Logger 实例
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")      // 从配置文件中读取密钥
    private String secret;
    @Value("${jwt.expiration}")  // Token 有效期（单位：毫秒）
    private Long expiration;

    // 生成安全的密钥
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 修复编码问题
    }

    // 生成 Token
    public String generateToken(Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    // 解析 Token
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Token 解析失败：{}", e.getMessage()); // 使用 logger 记录日志
            throw e;
        }
    }

    // 从 Token 中获取用户 ID
    public Integer getUserId(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("userId");
    }

    // 验证 Token 是否过期
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
    public Integer getUserIdFromHeader(String authHeader) {
        logger.info("Authorization Header: |{}|", authHeader); // 显示原值是否有空格污染
        String token = authHeader.substring(7).trim(); // 增加 trim() 清除首尾空格
        return getUserId(token);
    }
}