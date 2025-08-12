package com.hallbooking.vendor.repository;

import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByEmail(String email);

    List<Vendor> findByStatus(VendorStatus status);

    // ✅ New: Support lookup by phone and country code
    Optional<Vendor> findByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);
}
