package com.hallbooking.notification.listener;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.OtpNotificationRequest;
import com.hallbooking.notification.service.EmailServiceImpl;
import com.hallbooking.notification.service.SmsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OTPQueueListener {

    private final EmailServiceImpl emailService;
    private final SmsServiceImplementation smsService;

    @RabbitListener(queues = "otp_queue")
    public void listenOtpQueue(OtpNotificationRequest request) {
        System.out.println("📨 OTP Message Received in OTPQueueListener: " + request);
        try {
            if (request.getType() == NotificationType.EMAIL) {
                emailService.sendOtp(request.getTo(), request.getOtp());
            } else if (request.getType() == NotificationType.SMS) {
                smsService.sendOtp(request.getTo(), request.getOtp());
            } else {
                System.out.println("❓ Unknown OTP Type: " + request.getType());
            }
            System.out.println("✅ OTP processed for: " + request.getTo());
        } catch (Exception e) {
            System.out.println("❌ Failed to process OTP for " + request.getTo() + ": " + e.getMessage());
        }
    }
}
