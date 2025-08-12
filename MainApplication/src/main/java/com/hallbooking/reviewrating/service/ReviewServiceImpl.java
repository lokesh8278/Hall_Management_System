package com.hallbooking.reviewrating.service;


import com.hallbooking.reviewrating.dto.ReviewRequest;
import com.hallbooking.reviewrating.entity.Review;
import com.hallbooking.reviewrating.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Review addReview(ReviewRequest request) {
        Review review = Review.builder()
                .userId(request.getUserId())
                .hallId(request.getHallId())
                .vendorId(request.getVendorId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByHall(Long hallId) {
        return reviewRepository.findByHallId(hallId);
    }

    @Override
    public List<Review> getReviewsByVendor(Long vendorId) {
        return reviewRepository.findByVendorId(vendorId);
    }
}
