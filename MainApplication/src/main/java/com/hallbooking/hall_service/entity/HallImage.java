package com.hallbooking.hall_service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
    @Table(name = "hall_images")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class HallImage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String imageUrl;
        @ManyToOne
        @JoinColumn(name = "hall_id")
        private Hall hall;



    }


