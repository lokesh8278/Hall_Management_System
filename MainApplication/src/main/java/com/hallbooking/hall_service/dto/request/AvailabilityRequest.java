package com.hallbooking.hall_service.dto.request;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AvailabilityRequest {
        private String date; // Format: YYYY-MM-DD
    }


