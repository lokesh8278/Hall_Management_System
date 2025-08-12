package com.hallbooking.ticketing.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketRequest {
    private Long userId;
    private String subject;
    private String description;
}

