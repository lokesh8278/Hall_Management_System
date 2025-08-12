package com.hallbooking.bookingService.entity;

import com.hallbooking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "waitlist")
public class WaitlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long hallId;

    private Long roomId;

    private Date checkIn;

    private Date checkOut;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Date createdAt = new Date();
}

