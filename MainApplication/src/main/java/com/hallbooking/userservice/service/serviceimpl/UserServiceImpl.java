package com.hallbooking.userservice.service.serviceimpl;

import com.hallbooking.userservice.dto.request.ChangePasswordRequest;
import com.hallbooking.userservice.dto.request.ProfileUpdateRequest;
import com.hallbooking.userservice.dto.response.UserProfileResponse;
import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import com.hallbooking.userservice.service.UserService;
import com.hallbooking.utilis.JWTUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;



    @Override
    public List<UserProfileResponse> getAllUserProfiles() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            UserProfileResponse res = new UserProfileResponse();
            res.setName(user.getName());
            res.setEmail(user.getEmail());
            res.setMobileNumber(user.getMobile());
            res.setDob(user.getDob());
            res.setGender(user.getGender());
            res.setId(user.getId());
            res.setCountryCode(user.getCountryCode());
            // add more fields if needed
            return res;
        }).collect(Collectors.toList());
    }



    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String fullMobile;

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            fullMobile = userDetails.getUsername(); // Should be in format: +91|9999339933
        } else {
            fullMobile = principal.toString();
        }

        System.out.println("Authenticated Mobile: " + fullMobile);

        // Split into country code and mobile number
        String[] parts = fullMobile.split("\\|");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token subject format");
        }

        String countryCode = parts[0];
        String mobile = parts[1];

        return userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new RuntimeException("User not found with mobile: " + mobile + " and country code: " + countryCode));
    }



    @Override
    public UserProfileResponse getProfile() {
        User user = getCurrentUser();
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.isActive(), user.getGender(),user.getDob(),user.getMobile(), user.getCountryCode());
    }


    @Override
    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
  //      user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());               // ✅ map gender
        user.setDob(request.getDob());                     // ✅ map dob
        user.setMobile(request.getMobile());
        user.setCountryCode(request.getCountryCode());

        userRepository.save(user);
        return new UserProfileResponse(user.getId(),user.getName(), user.getEmail(), user.isActive(), user.getGender(), user.getDob(),user.getMobile(), user.getCountryCode());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        // 1. Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        // 2. Optional: Prevent reusing same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from old password.");
        }

        // 3. Save the new encoded password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
