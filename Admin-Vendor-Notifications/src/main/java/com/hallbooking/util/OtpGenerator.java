package com.hallbooking.util;

import java.security.SecureRandom;

public class OtpGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        return String.format("%06d", random.nextInt(999999));
    }
}
