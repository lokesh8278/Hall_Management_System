package com.hallbooking.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingId; // link to booking service
    private Double amount;
    private String currency;
    private String paymentProvider; // STRIPE or RAZORPAY
    private String transactionId;
    private String status; // PENDING, SUCCESS, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();
}

