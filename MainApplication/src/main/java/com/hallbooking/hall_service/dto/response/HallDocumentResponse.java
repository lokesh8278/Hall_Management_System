package com.hallbooking.hall_service.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HallDocumentResponse {
    private Long id;
    private String fileName;
    private String description;
    private String downloadUrl;
}
