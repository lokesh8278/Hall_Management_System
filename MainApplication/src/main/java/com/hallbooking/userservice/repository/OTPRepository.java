package com.hallbooking.userservice.repository;

import com.hallbooking.userservice.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP,Long> {
    Optional<OTP> findTopByEmailOrderByExpiryTimeDesc(String email);
}
