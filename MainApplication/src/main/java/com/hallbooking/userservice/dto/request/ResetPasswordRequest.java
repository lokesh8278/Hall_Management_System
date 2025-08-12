package com.hallbooking.userservice.dto.request;

import com.hallbooking.userservice.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@PasswordMatches
@Data
public class ResetPasswordRequest {

    @NotBlank
    private String mobile;

    @NotBlank
    private String countryCode;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
