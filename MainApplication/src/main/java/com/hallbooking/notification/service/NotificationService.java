package com.hallbooking.notification.service;

import com.hallbooking.notification.dto.NotificationRequest;

public interface NotificationService {
    void sendNotification(NotificationRequest request);
}
