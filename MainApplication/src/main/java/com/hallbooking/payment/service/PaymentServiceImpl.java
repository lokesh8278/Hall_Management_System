package com.hallbooking.payment.service;

import com.hallbooking.payment.dto.request.PaymentRequest;
import com.hallbooking.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
            return stripeService.createPaymentLink(request);

    }

    @Override
    public void handleWebhook(String payload, String signature, String provider) {
            stripeService.handleWebhook(payload, signature);

    }
}

