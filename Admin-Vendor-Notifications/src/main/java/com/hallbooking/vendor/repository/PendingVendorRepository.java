package com.hallbooking.vendor.repository;

import com.hallbooking.vendor.model.PendingVendorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingVendorRepository extends JpaRepository<PendingVendorRequest, Long> {

    // ✅ For login or duplicate check
    Optional<PendingVendorRequest> findByEmail(String email);

    // ✅ Optional: Filter by role (useful if needed)
    List<PendingVendorRequest> findAllByRole(String role);

    // ✅ New: For OTP-based operations
    Optional<PendingVendorRequest> findByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);
}
