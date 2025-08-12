package com.hallbooking.payment.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String bookingId;
    private Double amount;
    private String currency;
    private String paymentProvider; // STRIPE or RAZORPAY
}

