package com.hallbooking.config;

import com.hallbooking.bookingService.dto.request.SmsNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("✅ Message successfully published to RabbitMQ.");
            } else {
                System.err.println("❌ Message failed to publish: " + cause);
            }
        });
    }

    public void sendSms(String phoneNumber, String text) {
        SmsNotification sms = new SmsNotification();
        sms.setPhoneNumber(phoneNumber);
        sms.setMessage(text);
        rabbitTemplate.convertAndSend("sms.exchange", "sms.send", sms);
    }


}
