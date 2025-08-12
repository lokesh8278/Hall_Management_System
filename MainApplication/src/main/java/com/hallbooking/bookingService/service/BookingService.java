package com.hallbooking.bookingService.service;

import com.hallbooking.bookingService.dto.request.WaitList;
import com.hallbooking.bookingService.dto.response.BookingResponseDTO;
import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.bookingService.entity.WaitlistEntity;

import java.util.List;

public interface BookingService {

    // Updated method signature to include required IDs
    String addBookings(Bookings bookings, Long userId, Long hallId, Long roomId);

    public List<WaitlistEntity> getWaitlistedUsers();
    List<BookingResponseDTO> getBookingsByUserId(Long userId);
    List<BookingResponseDTO> getAllBookings();

    List<BookingResponseDTO> getBookingsByVendorId(Long vendorId);
    void cancelBooking(int id);
}
