package com.hallbooking.userservice.service.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public JwtRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ✅ Stores valid JWT session (optional)
    public void storeToken(String token, Long userId, long ttlMillis) {
        redisTemplate.opsForValue().set("JWT_" + token, "valid", ttlMillis, TimeUnit.MILLISECONDS);

        String userKey = "USER_TOKENS_" + userId;
        redisTemplate.opsForSet().add(userKey, token);

        console.log("🟢 Stored valid JWT token for user " + userId);
    }

    // ✅ Blacklist check using only 'blacklist:' key
    public boolean isTokenBlacklisted(String token) {
        boolean blacklisted = Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
        console.log("🧾 Checking blacklist:" + token + " => " + blacklisted);
        return blacklisted;
    }

    // ✅ Explicit blacklisting method (e.g. on logout)
    public void blacklistToken(String token) {
        redisTemplate.opsForValue().set("blacklist:" + token, "true");
        console.log("⛔ Token blacklisted: " + token);
    }

    // ✅ Remove token from Redis manually (optional)
    public void deleteToken(String token, Long userId) {
        redisTemplate.delete("JWT_" + token);
        redisTemplate.opsForSet().remove("USER_TOKENS_" + userId, token);
        console.log("🧹 Deleted JWT_" + token + " for user " + userId);
    }

    // ✅ Clean all tokens for a user (optional use case)
    public void deleteAllTokensForUser(Long userId) {
        String userKey = "USER_TOKENS_" + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(userKey);
        if (tokens != null) {
            for (Object token : tokens) {
                redisTemplate.delete("JWT_" + token);
            }
        }
        redisTemplate.delete(userKey);
        console.log("🧹 Cleared all JWT tokens for user " + userId);
    }

    // ✅ Simple logger wrapper for console output
    private static class console {
        public static void log(String msg) {
            System.out.println(msg);
        }
    }
}
