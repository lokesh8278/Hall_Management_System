package com.hallbooking.bookingService.dto.request;

import com.hallbooking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitList implements Serializable {
    private long userId;
    private long hallId;
    private long roomid;
    private Date checkIn;
    private Date checkOut;
    private Date createdat=new Date();
    private BookingStatus status;
    }
