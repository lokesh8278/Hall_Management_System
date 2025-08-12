package com.hallbooking.admin.repository;

import com.hallbooking.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);

}