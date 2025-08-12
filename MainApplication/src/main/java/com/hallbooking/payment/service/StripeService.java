package com.hallbooking.payment.service;



import com.hallbooking.payment.dto.request.PaymentRequest;
import com.hallbooking.payment.dto.response.PaymentResponse;
import com.hallbooking.payment.entity.Payment;
import com.hallbooking.payment.repository.PaymentRepository;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPaymentLink(PaymentRequest request) {
        Stripe.apiKey = "sk_test_key_should_be_from_config"; // Replace in config later

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://yourapp.com/success")
                .setCancelUrl("https://yourapp.com/cancel")
                .addAllLineItem(List.of(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(request.getCurrency())
                                                .setUnitAmount((long)(request.getAmount() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Booking Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                ))
                .build();

        try {
            Session session = Session.create(params);
            Payment payment = Payment.builder()
                    .bookingId(request.getBookingId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .paymentProvider("STRIPE")
                    .transactionId(session.getId())
                    .status("PENDING")
                    .build();
            paymentRepository.save(payment);

            return new PaymentResponse(session.getUrl(), payment.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Stripe payment error", e);
        }
    }

    public void handleWebhook(String payload, String signature) {
        // For brevity, we simulate a webhook
        // Update payment status by transactionId
    }
}
