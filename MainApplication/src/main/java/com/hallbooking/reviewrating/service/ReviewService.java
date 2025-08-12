package com.hallbooking.reviewrating.service;


import com.hallbooking.reviewrating.dto.ReviewRequest;
import com.hallbooking.reviewrating.entity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(ReviewRequest request);
    List<Review> getReviewsByHall(Long hallId);
    List<Review> getReviewsByVendor(Long vendorId);
}
