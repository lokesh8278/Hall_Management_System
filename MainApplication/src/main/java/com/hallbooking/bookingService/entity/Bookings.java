package com.hallbooking.bookingService.entity;

import com.hallbooking.enums.BookingStatus;
import com.hallbooking.hall_service.entity.Hall;
import com.hallbooking.hall_service.entity.Room;
import com.hallbooking.userservice.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date checkin;

    private Date checkout;
    @Column(name="created_at")
    private Date createdat=new Date();
    private String status= String.valueOf(BookingStatus.PENDING);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hallid", nullable = false)
    private Hall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomid", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;


    private String qrCodePath;


}
