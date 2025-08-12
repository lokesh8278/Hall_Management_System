package com.hallbooking.userservice.service;

import com.hallbooking.userservice.dto.request.LoginRequest;
import com.hallbooking.userservice.dto.request.RegisterRequest;
import com.hallbooking.userservice.dto.response.LoginResponse;
import com.hallbooking.userservice.dto.response.MessageResponse;

public interface AuthService {
    MessageResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    public String forgotPassword(String mobile, String countryCode);
    public void resetPassword(String mobile, String countryCode, String newPassword);
    public void logout(String token);
    public void logoutFromAllDevices(String token);
    boolean verifyOtp(String mobile, String countryCode, String otp);

}
