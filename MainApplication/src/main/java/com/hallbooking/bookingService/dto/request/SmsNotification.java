package com.hallbooking.bookingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsNotification {
    private String phoneNumber;
    private String message;

    // Getters & setters
}
