package com.hallbooking.hall_service.dto.response;
import lombok.*;

import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class HallResponse {
        private Long id;
        private String name;
        private Long ownerId;
        private List<String> amenities;
        private String virtualTourUrl;
        private double basePrice;
        private List<HallDocumentResponse> documents;
        private String thumbnailUrl;

        private int guestCapacity;
        private double rating;
        private String contactNumber;
        private double latitude;
        private double longitude;


    }


