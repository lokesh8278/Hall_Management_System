package com.hallbooking.hall_service.entity;
import com.hallbooking.bookingService.entity.Bookings;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
    @Table(name = "rooms")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Room {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private int capacity;
        private double price;

        @ManyToOne
        @JoinColumn(name = "hall_id")
        private Hall hall;
        @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
        private List<Bookings> bookings;

    }

