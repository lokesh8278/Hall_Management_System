package com.hallbooking.config;

import com.hallbooking.bookingService.dto.request.WaitList;
import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.bookingService.service.BookingServiceImpl;
import com.hallbooking.enums.BookingStatus;
import com.hallbooking.bookingService.repository.BookingRepo;
import com.hallbooking.hall_service.entity.Hall;
import com.hallbooking.hall_service.entity.Room;
import com.hallbooking.hall_service.repository.HallRepository;
import com.hallbooking.hall_service.repository.RoomRepository;
import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

@Slf4j
@Component
public class NotificationConsumer {

    @Autowired private BookingRepo bookingRepo;
    @Autowired private UserRepository userRepository;
    @Autowired private HallRepository hallRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private BookingServiceImpl bookingService;

    @RabbitListener(queues = "waitlist.queue")
    public void handleWaitlistMessage(WaitList msg) {

        log.info("📩 Received waitlist message: {}", msg);

        // Basic validation to avoid NullPointer or invalid fetch
        if (msg.getUserId() <= 0 || msg.getHallId() <= 0 || msg.getRoomid() <= 0 ||
                msg.getCheckIn() == null || msg.getCheckOut() == null) {
            log.error("❌ Invalid WaitList message received: {}", msg);
            return;
        }

        long conflicts = bookingRepo.countConflictingBookings(
                msg.getHallId(), msg.getCheckIn(), msg.getCheckOut());



        if (conflicts == 0) {
            User user = userRepository.findById(msg.getUserId()).orElse(null);
            Hall hall = hallRepository.findById(msg.getHallId()).orElse(null);
            Room room = roomRepository.findById(msg.getRoomid()).orElse(null);

            if (user == null || hall == null || room == null) {
                log.error("❌ Could not fetch required entities. user: {}, hall: {}, room: {}",
                        msg.getUserId(), msg.getHallId(), msg.getRoomid());
                return;
            }

            Bookings booking = new Bookings();
            booking.setUser(user);
            booking.setHall(hall);
            booking.setRoom(room);
            booking.setCheckin(msg.getCheckIn());
            booking.setCheckout(msg.getCheckOut());
            booking.setStatus(BookingStatus.CONFIRMED.name());
            booking.setCreatedat(new Date());

            bookingRepo.save(booking);
            log.info("✅ Waitlisted user promoted to CONFIRMED booking.");
        } else {
            log.info("❌ Slot still full. Cannot promote waitlisted user with userId={}", msg.getUserId());
        }
    }
}
