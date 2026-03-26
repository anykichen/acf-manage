package com.acf.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@SpringBootTest
public class JwtUtilTest {

    @Test
    public void testGenerateAndParseToken() {
        // 测试生成和解析Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        String token = JwtUtil.generateToken(claims);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 解析Token
        Map<String, Object> parsedClaims = JwtUtil.parseToken(token);
        assertNotNull(parsedClaims);
        assertEquals(1L, parsedClaims.get("userId"));
        assertEquals("testuser", parsedClaims.get("username"));
    }

    @Test
    public void testTokenExpiration() {
        // 测试Token过期
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "testuser");
        
        // 生成1秒后过期的Token
        String token = JwtUtil.generateToken(claims, 1000);
        
        try {
            Thread.sleep(1500);
            Map<String, Object> parsedClaims = JwtUtil.parseToken(token);
            // Token应该已过期
            assertNull(parsedClaims);
        } catch (Exception e) {
            // 预期异常
            assertTrue(true);
        }
    }

    @Test
    public void testInvalidToken() {
        // 测试无效Token
        Map<String, Object> parsedClaims = JwtUtil.parseToken("invalid.token.here");
        assertNull(parsedClaims);
    }
}
