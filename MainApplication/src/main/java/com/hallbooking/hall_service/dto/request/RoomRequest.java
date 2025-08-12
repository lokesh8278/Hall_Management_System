package com.hallbooking.hall_service.dto.request;

import lombok.*;

@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RoomRequest {
        private String name;
        private Integer capacity;
        private Double price;
    }


