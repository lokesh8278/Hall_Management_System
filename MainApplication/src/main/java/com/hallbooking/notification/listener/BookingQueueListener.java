package com.hallbooking.notification.listener;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.service.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class BookingQueueListener {
    private final EmailServiceImpl emailService;

    @RabbitListener(queues = "booking_queue")
    public void listenBookingQueue(EmailRequest request) {
        System.out.println("📩 Booking Email Received: " + request);
        emailService.sendEmail(request);
    }
}
