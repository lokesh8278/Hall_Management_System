package com.hallbooking.bookingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HallDTO {
    private Long id;
    private String name;
    private Double baseprice;
    private double lat;
    private double lng;
}

