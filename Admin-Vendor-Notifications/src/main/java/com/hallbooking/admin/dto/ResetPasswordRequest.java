package com.hallbooking.admin.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String phoneNumber;
    private String otp;
    private String newPassword;
}
