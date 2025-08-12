package com.hallbooking.vendor.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String countryCode;  // e.g., "+91"
    private String phoneNumber;

}
