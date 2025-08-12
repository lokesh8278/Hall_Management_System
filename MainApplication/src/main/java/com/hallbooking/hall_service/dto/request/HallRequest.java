package com.hallbooking.hall_service.dto.request;

import lombok.*;

import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class HallRequest {
        private String name;
        private double lat;
        private double lng;
        private double basePrice;
        private String virtualTourUrl;
        private List<String> amenities;
    }



