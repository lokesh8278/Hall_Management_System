package com.hallbooking.notification.repository;
import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipient(String recipient);
    List<NotificationLog> findByType(NotificationType type);

    // ✅ Add this method to fix the error:
    Optional<NotificationLog> findTopByTypeAndRecipientOrderByCreatedAtDesc(NotificationType type, String recipient);
}
