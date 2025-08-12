package com.hallbooking.hall_service.dto.response;
import lombok.*;

import java.util.List;

@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AvailabilityResponse {
        private boolean available;
        private String message;
        private List<Long> availableRooms;
    }

