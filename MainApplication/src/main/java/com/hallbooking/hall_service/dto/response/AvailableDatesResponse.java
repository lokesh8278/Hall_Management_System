package com.hallbooking.hall_service.dto.response;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AvailableDatesResponse {
        private Long hallId;
        private List<LocalDate> availableDates;
    }


