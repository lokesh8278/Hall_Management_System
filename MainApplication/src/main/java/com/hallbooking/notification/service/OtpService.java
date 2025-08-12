package com.hallbooking.notification.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final StringRedisTemplate redisTemplate;

    private static final Duration OTP_TTL= Duration.ofMinutes(5);

    public void storeOtp(String identifier,String otp){
        String redisKey ="otp:value"+identifier;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL);
    }
    public boolean verifyOtp(String identifier, String userInputOtp) {
        String redisKey = "otp:value:" + identifier;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp != null && storedOtp.equals(userInputOtp)) {
            // OTP is valid, remove it after use
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }
    public void invalidateOtp(String identifier) {
        String redisKey = "otp:value:" + identifier;
        redisTemplate.delete(redisKey);
    }

}
