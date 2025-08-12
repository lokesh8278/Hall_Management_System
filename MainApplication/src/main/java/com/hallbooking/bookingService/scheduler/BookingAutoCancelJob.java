package com.hallbooking.bookingService.scheduler;

import com.hallbooking.config.NotificationProducer;
import com.hallbooking.bookingService.dto.request.SlotReleasedEvent;
import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.enums.BookingStatus;
import com.hallbooking.bookingService.repository.BookingRepo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingAutoCancelJob implements Job {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public void execute(JobExecutionContext context) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);



        List<Bookings> bookingsToCancel = bookingRepo
                .findByStatusAndCreatedatBefore(String.valueOf(BookingStatus.PENDING), cutoffTime);
        System.out.println("⏰ Auto-cancelled job completed {} bookings cancelled. " + bookingsToCancel.size());

        for (Bookings booking : bookingsToCancel) {
            booking.setStatus(String.valueOf(BookingStatus.CANCELLED));
            bookingRepo.save(booking);

//            RabbitTemplate rabbitTemplate;

//            notificationProducer.sendBookingCancelledNotification(booking);

            SlotReleasedEvent event = new SlotReleasedEvent();
            event.setHallId(booking.getHall().getId());
            event.setRoomId(booking.getRoom().getId());
            event.setUserId(booking.getUser().getId());
            event.setCheckIn(booking.getCheckin());
            event.setCheckOut(booking.getCheckout());

            rabbitTemplate.convertAndSend("waitlist.queue", event);
            System.out.print("Booking has been Auto-cancelled");
            System.out.println("⏰ Auto-cancelled booking ID: " + booking.getId());
        }
        System.out.println("⏰ Auto-cancelled job completed {} bookings cancelled. " + bookingsToCancel.size());
    }
}
