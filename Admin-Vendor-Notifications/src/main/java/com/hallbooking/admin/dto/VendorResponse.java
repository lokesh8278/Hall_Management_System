package com.hallbooking.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String status;


}
