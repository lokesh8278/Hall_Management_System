package com.hallbooking.userservice.controller;
import com.hallbooking.userservice.dto.request.*;
import com.hallbooking.userservice.dto.response.LoginResponse;
import com.hallbooking.userservice.dto.response.MessageResponse;
import com.hallbooking.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String response = authService.forgotPassword(request.getMobile(), request.getCountryCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean isVerified = authService.verifyOtp(
                request.getMobile(),
                request.getCountryCode(),
                request.getOtp()
        );

        if (isVerified) {
            return ResponseEntity.ok("✅ OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("❌ Invalid or expired OTP");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(
                    request.getMobile(),
                    request.getCountryCode(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("✅ Password reset successful");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage()); // Return the actual message to frontend
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logoutFromAllDevices(token);
        return ResponseEntity.ok("Logged out from all devices");
    }

}
