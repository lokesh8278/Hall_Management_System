package com.hallbooking.hall_service.dto.response;


import jdk.jshell.Snippet;
import lombok.*;

@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RoomResponse {
        private Long id;
        private String name;
        private int capacity;
        private double price;


    }


