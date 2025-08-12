package com.hallbooking.vendor.service;

import java.util.List;

import com.hallbooking.vendor.dto.*;

public interface VendorService {

    // 🔐 Auth
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);

    // 📝 Registration Flow
    String registerRequest(RegisterRequest request);   // sends pending request + email
    String savePendingVendor(RegisterRequest request); // internal use

    // 🔄 Profile Management
    void updateVendor(Long id, RegisterRequest request);
    void deleteVendor(Long id);
    void updateFeatures(Long vendorId, UpdateFeaturesRequest request);

    // 🔍 Verification
    boolean verifyToken(String token);
    VerifyResponse getVerifyInfo(String token);

    // ✅ Vendor Approval Flow (Admin Side)
    List<PendingVendorDTO> getPendingVendors();
    String approveVendor(Long id);
    String rejectVendor(Long id);

    // 📋 Listing
    List<VendorResponse> getAllVendors();
    List<VendorResponse> getApprovedVendors();
    VendorResponse getVendorById(Long id);
    public boolean verifyOtp(String countryCode, String phoneNumber, String otp);
    // 🔐 Forgot Password with OTP
    String sendOtpForPasswordReset(OtpRequest request);     // sends OTP to phone
    String resetPassword(ResetPasswordRequest request);     // resets password without verifying OTP again
}
