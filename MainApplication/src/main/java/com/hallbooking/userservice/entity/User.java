package com.hallbooking.userservice.entity;

import com.hallbooking.enums.Role;
import com.hallbooking.userservice.validation.ValidPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.hallbooking.bookingService.entity.Bookings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String gender;
    private LocalDate dob;
    @Column(name = "mobile_number", unique = true, nullable = false)
    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    private String mobile;
    @Column(name = "country_code", nullable = false)
    private String countryCode;
    @ValidPassword
    private String password;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    public boolean isActive() {
        return active;
    }
    // In User.java
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bookings> bookings;



}
