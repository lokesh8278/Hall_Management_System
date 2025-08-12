package com.hallbooking.admin.dto;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String phoneNumber;
    private String otp;
}
