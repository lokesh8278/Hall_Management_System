package com.hallbooking.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
    @NotBlank(message = "Recipient number is required")
    private String to;

    @NotBlank(message = "Message text is required")
    private String message;
}
