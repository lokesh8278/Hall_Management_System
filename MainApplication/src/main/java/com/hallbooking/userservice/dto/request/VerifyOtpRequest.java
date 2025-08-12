package com.hallbooking.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank
    private String mobile;

    @NotBlank
    private String countryCode;

    @NotBlank
    private String otp;

    // Getters and Setters
}
