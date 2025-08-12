package com.hallbooking.vendor.dto;



import lombok.Data;

    @Data
    public class OtpVerificationRequest {
        private String countryCode;
        private String phoneNumber;
        private String otp;
    }


