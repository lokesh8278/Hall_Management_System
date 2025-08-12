package com.hallbooking.reviewrating.repository;
import com.hallbooking.reviewrating.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHallId(Long hallId);
    List<Review> findByVendorId(Long vendorId);
}

