package com.hallbooking.notification.dto;



import com.hallbooking.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class OtpNotificationRequest implements Serializable {
    @NotBlank(message = "Recipient is required (email or phone)")
    private String to;

    //@NotBlank(message = "OTP code is required")
    private String otp;

    private NotificationType type;  // EMAIL or SMS
}
