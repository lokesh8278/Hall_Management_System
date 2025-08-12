package com.hallbooking.util;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OtpRateLimiter {
    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final long OTP_COOLDOWN_SECONDS = 60;

    public boolean isAllowed(String key) {
        String redisKey = "otp:rate:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, TTL);
        }
        return count != null && count <= MAX_ATTEMPTS;
    }

    public void reset(String key) {
        redisTemplate.delete("otp:rate:" + key);
    }

    public boolean canSendOtp(String userKey) {
        String key = "otp_limit:" + userKey;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) return false;
        redisTemplate.opsForValue().set(key, "LOCK", OTP_COOLDOWN_SECONDS, TimeUnit.SECONDS);
        return true;
    }
}

