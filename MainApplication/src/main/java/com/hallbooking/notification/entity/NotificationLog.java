package com.hallbooking.notification.entity;

import com.hallbooking.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;               // email or phone number

    private String subject;                 // only for email

    @Column(length = 5000)
    private String message;                 // email body or SMS text

    @Enumerated(EnumType.STRING)
    private NotificationType type;          // EMAIL or SMS

    private boolean success;

    private LocalDateTime sentAt;
}
