package com.hallbooking.admin.controller;

import com.hallbooking.admin.dto.*;
import com.hallbooking.admin.model.Admin;
import com.hallbooking.admin.service.AdminService;
import com.hallbooking.admin.service.VendorService;
import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.service.NotificationRouter;
import com.hallbooking.notification.service.OtpService;
import com.hallbooking.vendor.model.PendingVendorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private NotificationRouter notificationRouter;


    // ✅ Admin Login
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return adminService.login(request);
    }

    // ✅ Admin Registration with NO OTP
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        LoginResponse response = adminService.register(request);
        if ("Admin already exists with this email".equals(response.getToken())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // ✅ Forgot Password - Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Admin admin = adminService.findByPhoneNumber(request.getPhoneNumber()); // expects full phone like +91XXX
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin with this phone number not found");
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        String fullPhone = admin.getFullPhoneNumber(); // e.g. +91852xxxxxxx

        otpService.storeOtp(fullPhone, otp);

        notificationRouter.dispatch(
                NotificationType.SMS,
                fullPhone,
                "Reset Your Password",
                "Your OTP for password reset is: " + otp
        );

        return ResponseEntity.ok("✅ OTP sent to your phone number via SMS.");
    }

    // ✅ OTP Verification for Forgot Password
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        String phone = request.getPhoneNumber(); // should be full phone like +91XXX
        boolean isValid = otpService.verifyOtp(phone, request.getOtp());

        if (isValid) {
            return ResponseEntity.ok("✅ OTP verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid or expired OTP.");
        }
    }


    // ✅ Reset Password with OTP and Send Email Notification
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String fullPhone = request.getPhoneNumber();

        boolean valid = otpService.verifyOtp(fullPhone, request.getOtp());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid or expired OTP.");
        }

        boolean result = adminService.updatePassword(fullPhone, request.getNewPassword());

        if (result) {
            otpService.invalidateOtp(fullPhone); // ✅ delete OTP only after success

            // ✅ Send confirmation email
            Admin admin = adminService.findByPhoneNumber(fullPhone);
            if (admin != null && admin.getEmail() != null) {
                notificationRouter.dispatch(
                        NotificationType.EMAIL,
                        admin.getEmail(),
                        "Password Reset Successful",
                        "Hi Admin,\n\nYour password has been successfully reset. If you did not initiate this request, please contact support immediately."
                );
            }

            return ResponseEntity.ok("✅ Password updated and confirmation email sent.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Failed to update password.");
        }
    }



    // ✅ Token Verification
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = adminService.verifyToken(token);
        return isValid
                ? ResponseEntity.ok("Login successful")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
    }




    // ✅ Admin-only: All Vendors
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vendors")
    public ResponseEntity<List<VendorResponse>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    // ✅ Admin-only: Pending Vendors
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-vendors")
    public ResponseEntity<List<PendingVendorDTO>> getPendingVendors() {
        return ResponseEntity.ok(vendorService.getPendingVendors());
    }



    // ✅ Admin-only: Approve Vendor
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.approveVendor(id));
    }

    // ✅ Admin-only: Reject Vendor
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<String> rejectVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.rejectVendor(id));
    }
}
