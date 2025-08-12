package com.hallbooking.utilis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.hallbooking.utilis.OtpConstants.*;

@Component
@RequiredArgsConstructor
public class OtpRateLimiter {
    private final RedisTemplate<String, String> redisTemplate;

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
