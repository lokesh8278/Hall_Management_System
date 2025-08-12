package com.hallbooking.bookingService.controller;

import com.hallbooking.bookingService.dto.request.BookingRequest;
import com.hallbooking.bookingService.dto.response.BookingResponseDTO;
import com.hallbooking.bookingService.dto.response.HallDTO;
import com.hallbooking.bookingService.dto.response.RoomDTO;
import com.hallbooking.bookingService.dto.response.UserDTO;
import com.hallbooking.bookingService.entity.WaitlistEntity;
import com.hallbooking.config.NotificationProducer;
import com.hallbooking.bookingService.dto.request.WaitList;
import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.bookingService.exception.ResourceNotFoundException;
import com.hallbooking.bookingService.repository.BookingRepo;
import com.hallbooking.bookingService.service.BookingService;
import com.hallbooking.bookingService.service.PdfGeneratorService;
import com.hallbooking.bookingService.service.QrCodeService;
import com.hallbooking.hall_service.entity.Hall;
import com.hallbooking.hall_service.entity.Room;
import com.hallbooking.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    QrCodeService qrCodeService;
    @Autowired
    PdfGeneratorService pdfGeneratorService;
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    NotificationProducer notificationProducer;

    private final AuthenticationManager authenticationManager;
    public BookingController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?> getBooking(@PathVariable int id) {
        Optional<Bookings> optionalBooking = bookingRepo.findById(id);

        if (optionalBooking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        Bookings booking = optionalBooking.get();

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

        BookingResponseDTO response = new BookingResponseDTO(
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

        return ResponseEntity.ok(response);
    }


    @PostMapping("/addbooking")
    public ResponseEntity<?> addBooking(@RequestBody BookingRequest request) {
        Bookings booking = new Bookings();
        booking.setCheckin(request.getCheckin());
        booking.setCheckout(request.getCheckout());
        booking.setStatus("PENDING");
        String result = bookingService.addBookings(booking, request.getUserId(), request.getHallId(), request.getRoomId());

        if (result.equals("slot full")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Slot full. You have been added to the waitlist.");
        }
        return ResponseEntity.ok("Booking confirmed successfully.");
    }

    @GetMapping("/waitlist")
    public List<WaitlistEntity> getWaitlistedUsers() {
        return bookingService.getWaitlistedUsers();
    }




    @PutMapping("/{id}/reschedule")//; after getting hall service i need to add the check condition whether the hall and room is available or not
    public String reSchedule(@PathVariable int id,@RequestBody Bookings booking) {
        Bookings bookings = bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        bookings.setCheckin(booking.getCheckin());
        bookings.setCheckout(booking.getCheckout());
        bookingRepo.save(bookings);
        return "Your Booking Has Been Rescheduled";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable int id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok("❌ Booking cancelled successfully. Waitlist event triggered.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Booking not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("⚠️ Error cancelling booking: " + e.getMessage());
        }
    }



    @GetMapping(value = "/qr/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrPng(@PathVariable int id) throws Exception {
        Bookings booking = bookingRepo.findById(id).orElseThrow();

        String qrText = "Booking ID: " + booking.getId()
                + ", User: " + (booking.getUser() != null ? booking.getUser().getId() : "-")
                + ", Status: " + booking.getStatus();

        byte[] png = qrCodeService.generateQRCode(
                "Your Booking Is Completed Successfully\n" + qrText, 200, 200);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @GetMapping(value = "/ticket/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadTicket(@PathVariable int id) throws Exception {
        Bookings booking = bookingRepo.findById(id).orElseThrow();

        String qrText = "Booking ID: " + booking.getId()
                + ", User: " + (booking.getUser() != null ? booking.getUser().getId() : "-")
                + ", Status: " + booking.getStatus();

        byte[] qr = qrCodeService.generateQRCode(
                "Your Booking Is Completed Successfully\n" + qrText, 200, 200);

        byte[] pdf = pdfGeneratorService.generateBookingPDF(booking, qr);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("booking-" + booking.getId() + ".pdf")
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(pdf);
    }



    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserId(@PathVariable Long userId) {
        List<BookingResponseDTO> response = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN')") // optional if using method security
    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> response = bookingService.getAllBookings();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('HALL_VENDOR')")
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByVendor(@PathVariable Long vendorId) {
        List<BookingResponseDTO> response = bookingService.getBookingsByVendorId(vendorId);
        return ResponseEntity.ok(response);
    }



}
