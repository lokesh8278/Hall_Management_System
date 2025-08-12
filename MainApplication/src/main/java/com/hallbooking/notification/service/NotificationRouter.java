package com.hallbooking.notification.service;

import com.hallbooking.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRouter {
    private final EmailServiceImpl emailService;
    private final SmsServiceImplementation smsService;
    private final PushNotificationServiceImplementation pushService;

    public void dispatch(NotificationType type, String to, String message, String otp) {
        switch (type) {
            case EMAIL -> emailService.sendOtp(to, otp);
            case SMS -> smsService.sendOtp(to, otp);
            case PUSH -> pushService.sendPush(to, "OTP", "Your OTP is: " + otp);
        }
    }
}
