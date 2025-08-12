package com.hallbooking.reviewrating.controller;


import com.hallbooking.reviewrating.dto.ReviewRequest;
import com.hallbooking.reviewrating.entity.Review;
import com.hallbooking.reviewrating.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<Review>> getReviewsForHall(@PathVariable Long hallId) {
        return ResponseEntity.ok(reviewService.getReviewsByHall(hallId));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Review>> getReviewsForVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(reviewService.getReviewsByVendor(vendorId));
    }
}

