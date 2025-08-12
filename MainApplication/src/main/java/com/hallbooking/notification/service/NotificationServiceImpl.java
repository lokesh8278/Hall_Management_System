package com.hallbooking.notification.service;


import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.dto.NotificationRequest;
import com.hallbooking.notification.dto.SmsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailServiceImpl emailService;
    private final SmsServiceImplementation smsService;

    @Override
    public void sendNotification(NotificationRequest request) {
        if (request.getType() == NotificationType.EMAIL) {
            EmailRequest email = new EmailRequest();
            email.setTo(request.getRecipient());
            email.setSubject(request.getSubject());
            email.setBody(request.getMessage());
            email.setPlainMessage(request.getMessage());
            emailService.sendEmail(email);
        } else if (request.getType() == NotificationType.SMS) {
            SmsRequest sms = new SmsRequest();
            sms.setTo(request.getRecipient());
            sms.setMessage(request.getMessage());
            smsService.sendSms(sms);
        } else {
            throw new IllegalArgumentException("Unsupported notification type: " + request.getType());
        }
    }
}
