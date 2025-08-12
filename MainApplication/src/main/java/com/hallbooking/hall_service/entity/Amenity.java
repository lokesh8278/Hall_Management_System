package com.hallbooking.hall_service.entity;
import com.hallbooking.enums.AmenityType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
    @Table(name = "amenities")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Amenity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        private AmenityType type;

        // In Amenity.java
        @ManyToMany(mappedBy = "amenities")
        private List<Hall> halls;

    }


