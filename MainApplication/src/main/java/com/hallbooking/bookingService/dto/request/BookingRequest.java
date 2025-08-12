package com.hallbooking.bookingService.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class BookingRequest {
    private Long userId;
    private Long hallId;
    private Long roomId;
    private Date checkin;
    private Date checkout;
}

