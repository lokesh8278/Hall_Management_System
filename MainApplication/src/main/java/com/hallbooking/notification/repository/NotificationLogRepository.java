package com.hallbooking.notification.repository;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipient(String recipient);
    List<NotificationLog> findByType(NotificationType type);
}
