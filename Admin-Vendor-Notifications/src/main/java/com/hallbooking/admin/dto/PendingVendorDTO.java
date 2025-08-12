package com.hallbooking.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingVendorDTO {
	private Long id;
	private String name;
	private String email;
	private String countryCode;     // ✅ Added
	private String phoneNumber;     // ✅ Added

}
