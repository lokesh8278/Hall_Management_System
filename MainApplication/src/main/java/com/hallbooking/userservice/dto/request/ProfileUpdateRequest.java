package com.hallbooking.userservice.dto.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    @NotBlank
    private String name;
    @Email
    private String email;
    private String gender;
    private LocalDate dob;
    @Column(name = "mobile_number", unique = true)
    private String mobile;
    public String getEmail() {
        return email;
    }
    @NotBlank(message = "Country code is required")
    private String countryCode;

}
