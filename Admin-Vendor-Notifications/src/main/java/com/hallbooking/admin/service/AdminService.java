package com.hallbooking.admin.service;


import com.hallbooking.admin.dto.LoginRequest;
import com.hallbooking.admin.dto.LoginResponse;
import com.hallbooking.admin.dto.RegisterRequest;
import com.hallbooking.admin.model.Admin;

public interface AdminService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse register(RegisterRequest registerRequest);
    boolean verifyToken(String token);
    Admin findByPhoneNumber(String phoneNumber);
    boolean updatePassword(String phoneNumber, String newPassword);
}