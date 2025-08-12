package com.hallbooking.notification.service;

import com.hallbooking.config.TwilioProperties;
import com.hallbooking.notification.dto.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;
@Service

public class SmsServiceImplementation {

    private final TwilioProperties twilioProperties;
    private String twilioNumber;

    public SmsServiceImplementation(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
        this.twilioNumber= twilioProperties.getPhoneNumber();
    }

    public void sendSms(SmsRequest request) {
        System.out.println("To: " + request.getTo());
        System.out.println("Message: " + request.getMessage());
        System.out.println("From Twilio: " + twilioProperties.getPhoneNumber());

        Message.creator(
                new PhoneNumber(request.getTo()),
                new PhoneNumber(twilioNumber),
                request.getMessage()
        ).create();

        System.out.println("✅ SMS sent to " + request.getTo());
    }

    public void sendOtp(String to, String otp) {
        SmsRequest sms = new SmsRequest();
        sms.setTo(to);
        sms.setMessage("Your OTP is: " + otp);
        sendSms(sms);
    }
//private final TemplateRenderer templateRenderer;
//
//    public void sendOtp(String to, String otpCode) {
//        // 1. Prepare model for template rendering
//        Map<String, Object> model = new HashMap<>();
//        model.put("otp", otpCode);
//
//        // 2. Render SMS body using the otp-sms.ftl template
//        String message = templateRenderer.renderSmsTemplate("otp-sms.ftl", model);
//
//        // 3. Create and send SMS request
//        SmsRequest sms = new SmsRequest();
//        sms.setTo(to);
//        sms.setMessage(message);
//
//        sendSms(sms); // This should use your Twilio or SMS provider
//    }

}
