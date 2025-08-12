package com.hallbooking.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwilioInitializer {

    private final TwilioProperties props;

    @PostConstruct
    public void initTwilio() {
        System.out.println("🔍 Checking Twilio Config...");
        if (props.getAccountSid() == null || props.getAuthToken() == null) {
            throw new RuntimeException("❌ Twilio credentials are not injected");
        }
        Twilio.init(props.getAccountSid(), props.getAuthToken());
        System.out.println("✅ Twilio initialized");
    }
}
