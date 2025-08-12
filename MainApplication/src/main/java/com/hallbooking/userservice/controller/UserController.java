package com.hallbooking.userservice.controller;
import com.hallbooking.userservice.dto.request.ChangePasswordRequest;
import com.hallbooking.userservice.dto.request.ProfileUpdateRequest;
import com.hallbooking.userservice.dto.response.UserProfileResponse;
import com.hallbooking.userservice.service.UserService;
import com.hallbooking.userservice.sheduler.UserCleanupScheduler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCleanupScheduler userCleanupScheduler;

    @GetMapping("/getprofile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles() {
        List<UserProfileResponse> users = userService.getAllUserProfiles();
        System.out.println("✅ Returning users: " + users.size());
        return ResponseEntity.ok(userService.getAllUserProfiles());
    }



    @PutMapping("/profileupdate")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
        @PutMapping("/changepassword")
        public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
            userService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully");
        }

}
