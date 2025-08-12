package com.hallbooking.bookingService.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class SlotReleasedEvent {
    private long hallId;
    private long roomId;
    private long userId;
    private Date checkIn;
    private Date checkOut;
    private Date createdat = new Date();
}
