package com.hallbooking.payment.controller;

import com.hallbooking.payment.dto.request.PaymentRequest;
import com.hallbooking.payment.dto.response.PaymentResponse;
import com.hallbooking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiate(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @PostMapping("/webhook/{provider}")
    public ResponseEntity<Void> handleWebhook(
            @PathVariable String provider,
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") Optional<String> stripeSig,
            @RequestHeader("X-Razorpay-Signature") Optional<String> razorSig
    ) {
        String sig = stripeSig.orElse(razorSig.orElse(""));
        paymentService.handleWebhook(payload, sig, provider);
        return ResponseEntity.ok().build();
    }
}
