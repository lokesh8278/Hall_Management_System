package com.hallbooking.userservice.dto.response;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    private String gender;
    private LocalDate dob;
    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;
    @NotBlank(message = "Country code is required")
    private String countryCode;
    public boolean isActive() {
        return active;
    }

    public UserProfileResponse(long id,String name, String email, boolean active, String gender, LocalDate dob, String mobileNumber,String countryCode) {
        this.id=id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.gender = gender;
        this.dob = dob;
        this.mobileNumber = mobileNumber;
        this.countryCode=countryCode;
    }
}
