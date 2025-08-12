package com.hallbooking.vendor.service.impl;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.service.NotificationRouter;
import com.hallbooking.vendor.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailAsyncService {

    @Autowired
    private NotificationRouter notificationRouter;

    @Async
    public void sendLoginNotification(Vendor vendor) {
        notificationRouter.dispatch(
                NotificationType.EMAIL,
                vendor.getEmail(),
                "Vendor Login Successful",
                "You have successfully logged in as a vendor."
        );
    }
}

