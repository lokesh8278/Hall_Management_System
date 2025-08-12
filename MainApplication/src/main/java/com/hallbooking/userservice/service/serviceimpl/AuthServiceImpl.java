package com.hallbooking.userservice.service.serviceimpl;

import com.hallbooking.enums.Role;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.dto.SmsRequest;
import com.hallbooking.notification.service.EmailServiceImpl;
import com.hallbooking.notification.service.SmsServiceImplementation;
import com.hallbooking.userservice.dto.request.LoginRequest;
import com.hallbooking.userservice.dto.request.RegisterRequest;
import com.hallbooking.userservice.dto.response.LoginResponse;
import com.hallbooking.userservice.dto.response.MessageResponse;
import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import com.hallbooking.userservice.service.AuthService;
import com.hallbooking.utilis.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hallbooking.utilis.OtpRateLimiter;
import com.hallbooking.utilis.OtpGenerator;
import static com.hallbooking.utilis.OtpConstants.*;


import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JwtRedisService jwtRedisService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private SmsServiceImplementation smsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OtpRateLimiter otpRateLimiter;




    @Override
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByMobileAndCountryCode(request.getMobile(), request.getCountryCode())) {
            return new MessageResponse("Mobile number already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setGender(request.getGender());
        user.setDob(request.getDob());
        user.setMobile(request.getMobile());
        user.setCountryCode(request.getCountryCode());
        userRepository.save(user);

        // ✅ Send Email
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("🎉 Welcome to HallBooking!");
        emailRequest.setBody("Hello " + user.getName() + ",\n\nThank you for registering with HallBooking.\nEnjoy our platform!");
        emailService.sendEmail(emailRequest);

        // ✅ Send SMS
        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setTo(user.getCountryCode() + user.getMobile()); // e.g. +919876543210
        smsRequest.setMessage("👋 Hi " + user.getName() + "! Your HallBooking account was created successfully.");
        smsService.sendSms(smsRequest);

        return new MessageResponse(user.getName()+"  Registered successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByMobileAndCountryCode(request.getMobile(), request.getCountryCode())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.isActive()) {
            throw new RuntimeException("User is deactivated. Contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user); // save updated lastLoginAt


        String token = jwtUtil.generateToken(user);// store both country code + mobile in token if needed

        jwtRedisService.storeToken(token, user.getId(), 86400000);
        return new LoginResponse(token, user.getName(), user.getRole().name(), user.getId());
    }

    @Override
    public String forgotPassword(String mobile, String countryCode) {
        User user = userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userKey = countryCode + "|" + mobile;

        // ✅ Prevent frequent OTP spamming (cooldown of 60 sec)
        if (!otpRateLimiter.canSendOtp(userKey)) {
            throw new RuntimeException("Please wait before requesting a new OTP");
        }

        // ✅ Rate limiting (max 5 OTP requests in 5 minutes)
        if (!otpRateLimiter.isAllowed(userKey)) {
            throw new RuntimeException("Too many OTP requests. Try again later.");
        }

        // ✅ Generate OTP
        String otp = OtpGenerator.generateOtp();

        // ✅ Store in Redis
        String otpKey = "FORGOT_OTP_" + userKey;
        redisTemplate.opsForValue().set(otpKey, otp, 5, TimeUnit.MINUTES);

        // ✅ Send via SMS (or Email optionally)
        smsService.sendOtp(countryCode + mobile, otp);
//        System.out.println("Generated OTP for " + userKey + ": " + otp);

        return "OTP has been sent to your registered phone number.";
    }

    @Override
    public boolean verifyOtp(String mobile, String countryCode, String otp) {
        countryCode = countryCode.trim();
        mobile = mobile.trim();
        otp = otp.trim();
        // ✅ Ensure countryCode always starts with '+'
        if (!countryCode.startsWith("+")) {
            countryCode = "+" + countryCode;
        }
        String userKey = countryCode + "|" + mobile;
        String key = OTP_KEY_PREFIX + userKey;
        Object stored = redisTemplate.opsForValue().get(key);

        System.out.println("🔍 [OTP VERIFY]");
        System.out.println("Key Used: " + key);
        System.out.println("Entered OTP: " + otp);
        System.out.println("Stored OTP: " + stored);

        if (stored != null && stored.toString().equals(otp)) {
            redisTemplate.delete(key);
            // ✅ Set verified key in Redis
            String verifiedKey = OTP_VERIFIED_KEY_PREFIX + userKey;
            redisTemplate.opsForValue().set(
                    verifiedKey,
                    "true",
                    OTP_VERIFICATION_EXPIRY_MINUTES,
                    TimeUnit.MINUTES
            );
            System.out.println("✅ OTP verified. Setting verified flag: " + verifiedKey);
            otpRateLimiter.reset(userKey);
            return true;
        }
        System.out.println("❌ OTP verification failed.");
        return false;
    }


    @Override
    @Transactional
    public void resetPassword(String mobile, String countryCode, String newPassword) {
        countryCode = countryCode.trim();
        mobile = mobile.trim();

        // Ensure + is included
        if (!countryCode.startsWith("+")) {
            countryCode = "+" + countryCode;
        }

        String userKey = countryCode + "|" + mobile;

        String otpKey = OTP_KEY_PREFIX + userKey;
        String attemptKey = OTP_ATTEMPT_KEY_PREFIX + userKey;
        String verifiedKey = OTP_VERIFIED_KEY_PREFIX + userKey;

        // 🧪 Debug
        System.out.println("🔐 Checking verification flag in Redis:");
        System.out.println("Key: " + verifiedKey);

        // ✅ Must verify OTP first
        Object verified = redisTemplate.opsForValue().get(verifiedKey);
        System.out.println("Value: " + verified);

        if (verified == null || !"true".equals(verified.toString())) {
            throw new RuntimeException("OTP not verified. Please verify before resetting password.");
        }

        // ✅ Reset password
        User user = userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // ✅ Clean up Redis keys
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);
        redisTemplate.delete(verifiedKey);
    }

    @Override
    public void logout(String token) {
        String compositeMobile = jwtUtil.extractMobile(token);
        String[] parts = compositeMobile.split("\\|");
        String countryCode = parts[0];
        String mobile = parts[1];

        User user = userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtRedisService.deleteToken(token, user.getId());
    }

    @Override
    public void logoutFromAllDevices(String token) {
        String compositeMobile = jwtUtil.extractMobile(token);
        String[] parts = compositeMobile.split("\\|");
        String countryCode = parts[0];
        String mobile = parts[1];

        User user = userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtRedisService.deleteAllTokensForUser(user.getId());
    }





}
