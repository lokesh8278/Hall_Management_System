package com.hallbooking.notification.service;

import com.hallbooking.notification.dto.SmsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final SmsServiceImplementation smsService;

    private static final String OTP_PREFIX = "otp:value:"; // Redis key prefix

    /**
     * Stores OTP in Redis with no expiration.
     * Example key: otp:value:+918523067893
     */
    public void storeOtp(String fullPhoneNumber, String otp) {
        String redisKey = OTP_PREFIX + fullPhoneNumber;
        redisTemplate.opsForValue().set(redisKey, otp); // no TTL
        System.out.println("✅ Stored OTP [" + otp + "] for key: " + redisKey);
    }

    /**
     * Verifies the given OTP for the provided phone identifier.
     */
    public boolean verifyOtp(String identifier, String userInputOtp) {
        String redisKey = OTP_PREFIX + identifier;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);
        return storedOtp != null && storedOtp.equals(userInputOtp);
    }

    /**
     * Deletes OTP after successful verification or reset.
     */
    public void deleteOtp(String identifier) {
        redisTemplate.delete(OTP_PREFIX + identifier);
    }

    /**
     * Invalidates OTP manually (for admin/debug use).
     */
    public void invalidateOtp(String fullPhoneNumber) {
        String redisKey = OTP_PREFIX + fullPhoneNumber;
        redisTemplate.delete(redisKey);
        System.out.println("🧹 OTP manually invalidated for key: " + redisKey);
    }

    /**
     * ✅ Generates and stores OTP, logs it (use actual SMS integration in production).
     */
    public void sendOtp(String fullPhoneNumber) {
        String otp = generateOtp();
        storeOtp(fullPhoneNumber, otp);

        // TODO: Replace with SMS provider call
        System.out.println("📲 Sending OTP [" + otp + "] to phone: " + fullPhoneNumber);

        // Directly dispatch SMS to test
        SmsRequest sms = new SmsRequest();
        sms.setTo(fullPhoneNumber);
        sms.setMessage("Your OTP is: " + otp);
        smsService.sendSms(sms);
    }

    /**
     * Utility to generate 6-digit numeric OTP.
     */
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
