package com.hallbooking.userservice.service;

import com.hallbooking.userservice.dto.request.ChangePasswordRequest;
import com.hallbooking.userservice.dto.request.ProfileUpdateRequest;
import com.hallbooking.userservice.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getProfile();
    UserProfileResponse updateProfile(ProfileUpdateRequest request);
    void changePassword(ChangePasswordRequest request);
    List<UserProfileResponse> getAllUserProfiles();

}
