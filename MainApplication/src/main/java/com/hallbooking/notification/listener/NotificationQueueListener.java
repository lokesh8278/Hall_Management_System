package com.hallbooking.notification.listener;


import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.dto.OtpNotificationRequest;
import com.hallbooking.notification.dto.PushNotificationRequest;
import com.hallbooking.notification.dto.SmsRequest;
import com.hallbooking.notification.service.NotificationRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class NotificationQueueListener {
    private final NotificationRouter router;

    @RabbitListener(queues = "otp_queue")
    public void handleOtpQueue(OtpNotificationRequest request) {
        router.dispatch(request.getType(), request.getTo(), "Your Otp", request.getOtp());
    }

    @RabbitListener(queues = "email_queue")
    public void handleEmailQueue(EmailRequest request) {
        router.dispatch(NotificationType.EMAIL, request.getTo(), request.getSubject(), request.getBody());
    }


    @RabbitListener(queues = "sms_queue")
    public void handleSmsQueue(SmsRequest request){
        router.dispatch(NotificationType.SMS, request.getTo(), "your otp code",request.getMessage());
    }

    @RabbitListener(queues = "push_queue")
    public void handlePushQueue(PushNotificationRequest request) {
        router.dispatch(NotificationType.PUSH, request.getToken(),"your token:", request.getMessage());
    }
}
