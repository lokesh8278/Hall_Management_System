package com.hallbooking.utilis;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    public String generateOTP(int length) {
        return RandomStringUtils.randomNumeric(length);
    }
}