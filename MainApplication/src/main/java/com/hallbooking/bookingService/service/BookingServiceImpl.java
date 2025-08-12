package com.hallbooking.bookingService.service;

import com.hallbooking.bookingService.dto.request.WaitList;
import com.hallbooking.bookingService.dto.response.BookingResponseDTO;
import com.hallbooking.bookingService.dto.response.HallDTO;
import com.hallbooking.bookingService.dto.response.RoomDTO;
import com.hallbooking.bookingService.dto.response.UserDTO;
import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.bookingService.entity.WaitlistEntity;
import com.hallbooking.bookingService.exception.ResourceNotFoundException;
import com.hallbooking.bookingService.repository.BookingRepo;
import com.hallbooking.bookingService.repository.WaitlistRepository;
import com.hallbooking.config.NotificationProducer;
import com.hallbooking.config.RabbitMQConfig;
import com.hallbooking.enums.BookingStatus;
import com.hallbooking.hall_service.entity.Hall;
import com.hallbooking.hall_service.entity.Room;
import com.hallbooking.hall_service.repository.HallRepository;
import com.hallbooking.hall_service.repository.RoomRepository;
import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import com.hallbooking.userservice.service.serviceimpl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingRepo bookingRepository;

    @Autowired
    private NotificationProducer notificationProd;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private  BookingEmailService bookingEmailService;


    private Queue<WaitList> waitlistQueue = new ConcurrentLinkedQueue<>();

    @Override
    public List<WaitlistEntity> getWaitlistedUsers() {
        return waitlistRepository.findAll();
    }


    @RabbitListener(queues = RabbitMQConfig.WAITLIST_QUEUE)
    public void receiveWaitlistFromMQ(WaitList waitlist) {
        System.out.println("📥 Received WaitList from MQ: " + waitlist);
        waitlistQueue.add(waitlist); // ✅ THIS makes /waitlist return it
    }


    public String addBookings(Bookings bookings, Long userId, Long hallId, Long roomId) {
        // ✅ Set associated User, Hall, Room
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        bookings.setUser(user);
        bookings.setHall(hall);
        bookings.setRoom(room);
        bookings.setCreatedat(new Date());

        // Conflict check
        long conflicts = bookingRepository.countConflictingBookings(
                hallId, bookings.getCheckin(), bookings.getCheckout());

        if (conflicts > 0) {
            // Add to waitlist
            WaitlistEntity waitlisted = new WaitlistEntity();
            waitlisted.setUserId(user.getId());
            waitlisted.setHallId(hall.getId());
            waitlisted.setRoomId(room.getId());
            waitlisted.setCheckIn(bookings.getCheckin());
            waitlisted.setCheckOut(bookings.getCheckout());
            waitlisted.setStatus(BookingStatus.WAITLISTED);


            waitlistRepository.save(waitlisted); // ✅ Save in DB

            // ✅ Log before sending
            System.out.println("Sending waitlist message: " + waitlisted);
            // ✅ Also push to MQ
            rabbitTemplate.convertAndSend(RabbitMQConfig.WAITLIST_QUEUE, waitlisted);
            System.out.println("User added to waitlist due to slot conflict.");



            return "slot full";
        } else {
            // ✅ Optional: Generate QR Code path here
            bookings.setStatus(BookingStatus.CONFIRMED.toString());
            bookings.setQrCodePath("qr_codes/booking_" + System.currentTimeMillis() + ".png");

            bookingRepository.save(bookings);
            System.out.println("✅ Booking is confirmed and saved.");
            // ... save booking ...

            // inside BookingServiceImpl, around line ~130
            try {
                bookingEmailService.sendBookingConfirmation(bookings.getUser(), bookings, true);
            } catch (Exception e) {
                log.warn("Failed to send booking confirmation email for booking {}: {}", bookings.getId(), e.getMessage(), e);
            }

            return "booking confirmed";
        }
    }

    @Transactional
    public void cancelBooking(int bookingId) {
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(String.valueOf(BookingStatus.CANCELLED));
        bookingRepository.save(booking);

        WaitList waitList = new WaitList();
        waitList.setUserId(booking.getUser().getId());
        waitList.setHallId(booking.getHall().getId());
        waitList.setRoomid(booking.getRoom().getId());
        waitList.setCheckIn(booking.getCheckin());
        waitList.setCheckOut(booking.getCheckout());
        waitList.setStatus(BookingStatus.WAITLISTED);

        rabbitTemplate.convertAndSend("waitlist.queue", waitList);

        System.out.println("❌ Booking cancelled & waitlist user promotion message sent.");
    }
    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        List<Bookings> bookings = bookingRepository.findByUserIdWithDetails(userId);
        return bookings.stream().map(this::mapToBookingResponseDTO).collect(Collectors.toList());
    }
    
    private BookingResponseDTO mapToBookingResponseDTO(Bookings booking) {
        Hall hall = booking.getHall();
        Room room = booking.getRoom();
        User user = booking.getUser();

        return new BookingResponseDTO(
                booking.getId(),
                booking.getCheckin(),
                booking.getCheckout(),
                booking.getCreatedat(),
                booking.getStatus(),
                booking.getQrCodePath(),
                new HallDTO(hall.getId(), hall.getName(), hall.getBaseprice(), hall.getLatitude(), hall.getLongitude()),
                room != null ? new RoomDTO(room.getId(), room.getName(), room.getPrice()) : null,
                new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getMobile())
        );
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<Bookings> bookings = bookingRepository.findAll();

        return bookings.stream().map(booking -> {
            Hall hall = booking.getHall();
            Room room = booking.getRoom();
            User user = booking.getUser();

            HallDTO hallDTO = new HallDTO(
                    hall.getId(),
                    hall.getName(),
                    hall.getBaseprice(),
                    hall.getLatitude(),
                    hall.getLongitude()
            );

            RoomDTO roomDTO = new RoomDTO(
                    room.getId(),
                    room.getName(),
                    room.getPrice()
            );

            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getMobile()
            );

            return new BookingResponseDTO(
                    booking.getId(),
                    booking.getCheckin(),
                    booking.getCheckout(),
                    booking.getCreatedat(),
                    booking.getStatus(),
                    booking.getQrCodePath(),
                    hallDTO,
                    roomDTO,
                    userDTO
            );
        }).toList();
    }
    @Override
    public List<BookingResponseDTO> getBookingsByVendorId(Long vendorId) {
        List<Bookings> bookings = bookingRepository.findBookingsByVendorId(vendorId);

        return bookings.stream().map(booking -> {
            Hall hall = booking.getHall();
            Room room = booking.getRoom();
            User user = booking.getUser();

            return new BookingResponseDTO(
                    booking.getId(),
                    booking.getCheckin(),
                    booking.getCheckout(),
                    booking.getCreatedat(),
                    booking.getStatus(),
                    booking.getQrCodePath(),
                    new HallDTO(hall.getId(), hall.getName(), hall.getBaseprice(), hall.getLatitude(), hall.getLongitude()),
                    new RoomDTO(room.getId(), room.getName(), room.getPrice()),
                    new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getMobile())
            );
        }).collect(Collectors.toList());
    }


}
