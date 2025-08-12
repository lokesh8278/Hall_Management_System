package com.hallbooking.reviewrating.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long userId;
    private Long hallId;
    private Long vendorId;
    private int rating;
    private String comment;
}

