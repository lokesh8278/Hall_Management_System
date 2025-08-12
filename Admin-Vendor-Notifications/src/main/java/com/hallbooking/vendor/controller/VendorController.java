package com.hallbooking.vendor.controller;

import java.util.List;

import com.hallbooking.notification.service.OtpService;
import com.hallbooking.util.JwtUtil;
import com.hallbooking.vendor.dto.*;
import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.vendor.service.VendorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpService otpService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return vendorService.login(request);
    }

    @PostMapping("/register-request")
    public ResponseEntity<String> submitPendingVendor(@RequestBody RegisterRequest request) {
        System.out.println("📥 Incoming vendor registration request for: " + request.getEmail());
        String message = vendorService.registerRequest(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendOtpForReset(@RequestBody OtpRequest request) {
        String message = vendorService.sendOtpForPasswordReset(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = vendorService.resetPassword(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean isValid = vendorService.verifyOtp(
                request.getCountryCode(),
                request.getPhoneNumber(),
                request.getOtp()
        );

        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
        }
    }


    @GetMapping("/internal/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VendorResponse>> allVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @PutMapping("/features")
    public ResponseEntity<String> updateFeaturesForLoggedInVendor(
            @AuthenticationPrincipal Vendor vendor,
            @RequestBody UpdateFeaturesRequest request) {
        vendorService.updateFeatures(vendor.getId(), request);
        return ResponseEntity.ok("Features updated");
    }

    @GetMapping("/verify")
    public ResponseEntity<VerifyResponse> verifyToken(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(vendorService.getVerifyInfo(token));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateVendorOwnProfile(
            @AuthenticationPrincipal Vendor vendor,
            @RequestBody RegisterRequest updatedVendor) {
        vendorService.updateVendor(vendor.getId(), updatedVendor);
        return ResponseEntity.ok("Your profile has been updated successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteOwnVendorAccount(@AuthenticationPrincipal Vendor vendor) {
        vendorService.deleteVendor(vendor.getId());
        return ResponseEntity.ok("Your account has been deleted successfully");
    }
}
