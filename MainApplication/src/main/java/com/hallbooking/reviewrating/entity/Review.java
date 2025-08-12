package com.hallbooking.reviewrating.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long hallId;
    private Long vendorId; // Either hallId or vendorId should be set

    private int rating;

    @Column(length = 2000)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
