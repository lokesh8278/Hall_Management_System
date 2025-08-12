package com.hallbooking.bookingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private int id;
    private Date checkin;
    private Date checkout;
    private Date createdat;
    private String status;
    private String qrCodePath;
    private HallDTO hall;
    private RoomDTO room;
    private UserDTO user;
}


