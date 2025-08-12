package com.hallbooking.notification.dto;


import com.hallbooking.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String recipient;
    private String subject; // required only for email
    private String message;
    private NotificationType type;
}
