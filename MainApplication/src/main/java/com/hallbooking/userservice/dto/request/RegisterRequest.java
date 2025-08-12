package com.hallbooking.userservice.dto.request;

import com.hallbooking.userservice.validation.PasswordMatches;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@PasswordMatches
@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    private String gender;
    private LocalDate dob;
    @Column(name = "mobile_number", unique = true)
    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    private String mobile;
    @NotBlank(message = "Country code is required")
    private String countryCode;


}
