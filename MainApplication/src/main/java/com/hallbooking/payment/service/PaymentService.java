package com.hallbooking.payment.service;

import com.hallbooking.payment.dto.request.PaymentRequest;
import com.hallbooking.payment.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(PaymentRequest request);
    void handleWebhook(String payload, String signature, String provider);
}