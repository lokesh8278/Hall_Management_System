package com.hallbooking.notification.service;

import com.hallbooking.config.TwilioProperties;
import com.hallbooking.notification.dto.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImplementation {

    private final TwilioProperties twilioProperties;
    private String twilioNumber;

    public SmsServiceImplementation(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        this.twilioNumber = twilioProperties.getFromNumber();
        System.out.println("✅ Twilio From Number initialized: " + twilioNumber);
    }

    public void sendSms(SmsRequest request) {
        System.out.println("📤 Sending SMS...");
        System.out.println("To: " + request.getTo());
        System.out.println("Message: " + request.getMessage());
        System.out.println("From Twilio: " + twilioNumber);

        try {
            Message.creator(
                    new PhoneNumber(request.getTo()),
                    new PhoneNumber(twilioNumber),
                    request.getMessage()
            ).create();

            System.out.println("✅ SMS sent to " + request.getTo());
        } catch (Exception e) {
            System.out.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }

    public void sendOtp(String to, String otp) {
        SmsRequest sms = new SmsRequest();
        sms.setTo(to);
        sms.setMessage("Your OTP is: " + otp);
        sendSms(sms);
    }
}
