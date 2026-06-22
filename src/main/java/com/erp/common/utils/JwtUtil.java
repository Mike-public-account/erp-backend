package com.erp.common.utils;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT签发、解析工具
 */
public class JwtUtil {
    // 密钥 项目上线放入环境变量
    private static final String SECRET_STR = "erp2026secretkey123456erpbackend";
    // token有效期 7天 毫秒
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    private static SecretKey getSecretKey() {
        byte[] bytes = SECRET_STR.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 生成token，存储userId
     */
    public static String createToken(Long userId) {
        Date now = new Date();
        Date expireDate = DateUtil.offsetMillisecond(now, (int) EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析token获取userId
     */
    public static Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}