package com.hallbooking.utilis;

import java.time.Duration;

public class OtpConstants {
    public static final int MAX_ATTEMPTS = 5;
    public static final long OTP_COOLDOWN_SECONDS = 60;
    public static final Duration TTL = Duration.ofMinutes(5);
    public static final long OTP_BLOCK_TIME_MINUTES = 5;
    public static final int RESET_ATTEMPT_LIMIT = 3;
    public static final long OTP_VERIFICATION_EXPIRY_MINUTES = 10;

    // 🔑 Redis Key Prefixes
    public static final String OTP_KEY_PREFIX = "FORGOT_OTP_";
    public static final String OTP_VERIFIED_KEY_PREFIX = "OTP_VERIFIED_";
    public static final String OTP_ATTEMPT_KEY_PREFIX = "FORGOT_OTP_ATTEMPTS_";
    
}
