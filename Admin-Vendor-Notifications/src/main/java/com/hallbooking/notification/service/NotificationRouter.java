package com.hallbooking.notification.service;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.dto.SmsRequest;
import com.hallbooking.notification.entity.NotificationLog;
import com.hallbooking.notification.repository.NotificationLogRepository;
import jakarta.annotation.PreDestroy;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class NotificationRouter {

    private final EmailServiceImpl emailService;
    private final SmsServiceImplementation smsService;
    private final PushNotificationServiceImplementation pushService;
    private final NotificationLogRepository notificationLogRepository;

    // Match AsyncConfig: bean type is Executor, name = "notificationTaskExecutor"
    private final Executor notificationExecutor;

    public NotificationRouter(
            EmailServiceImpl emailService,
            SmsServiceImplementation smsService,
            PushNotificationServiceImplementation pushService,
            NotificationLogRepository notificationLogRepository,
            @Qualifier("notificationTaskExecutor") Executor notificationExecutor // <-- important
    ) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.notificationLogRepository = notificationLogRepository;
        this.notificationExecutor = notificationExecutor;
    }

    /* ================= Public APIs ================ */

    @Async("notificationTaskExecutor")
    public CompletableFuture<NotificationResult> dispatchAsync(
            NotificationType type, String to, String subject, String body) {
        try {
            NotificationLog attemptLog = logNotificationAttempt(type, to, subject, body);

            switch (type) {
                case EMAIL -> emailService.sendEmail(
                        EmailRequest.builder().to(to).subject(subject).body(body).build());
                case SMS -> smsService.sendSms(
                        SmsRequest.builder().to(to).message(body).build());
                case PUSH -> pushService.sendPush(to, subject, body);
            }

            logNotificationSuccess(type, to);
            return CompletableFuture.completedFuture(
                    NotificationResult.success(type, to, attemptLog.getId())
            );
        } catch (Exception e) {
            log.error("Failed to send {} to {}: {}", type, to, e.getMessage());
            logNotificationFailure(type, to, e.getMessage());
            return CompletableFuture.completedFuture(
                    NotificationResult.failure(type, to, e.getMessage())
            );
        }
    }

    @Async("notificationTaskExecutor")
    public void dispatch(NotificationType type, String to, String subject, String body) {
        dispatchAsync(type, to, subject, body);
    }

    @Async("notificationTaskExecutor")
    public CompletableFuture<List<NotificationResult>> dispatchBulk(List<NotificationRequest> requests) {
        List<CompletableFuture<NotificationResult>> futures = requests.stream()
                .map(r -> dispatchAsync(r.getType(), r.getRecipient(), r.getSubject(), r.getMessage()))
                .toList();
        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
    }

    /* ================= Logging (REQUIRES_NEW) ================ */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected NotificationLog logNotificationAttempt(
            NotificationType type, String recipient, String subject, String body) {
        NotificationLog logEntity = NotificationLog.builder()
                .type(type)
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        return notificationLogRepository.save(logEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void logNotificationSuccess(NotificationType type, String recipient) {
        latestByTypeAndRecipient(type, recipient).ifPresent(log -> {
            log.setStatus("SENT");
            log.setSentAt(LocalDateTime.now());
            notificationLogRepository.save(log);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void logNotificationFailure(NotificationType type, String recipient, String error) {
        latestByTypeAndRecipient(type, recipient).ifPresent(log -> {
            log.setStatus("FAILED");
            log.setMessage(error); // or use a separate error field if you have one
            notificationLogRepository.save(log);
        });
    }

    private Optional<NotificationLog> latestByTypeAndRecipient(NotificationType type, String recipient) {
        return notificationLogRepository.findTopByTypeAndRecipientOrderByCreatedAtDesc(type, recipient);
    }

    /* ================= Graceful shutdown ================ */

    @PreDestroy
    public void shutdownExecutor() {
        try {
            if (notificationExecutor instanceof ThreadPoolTaskExecutor t) {
                log.info("Shutting down notificationTaskExecutor...");
                t.shutdown();
            }
        } catch (Exception e) {
            log.warn("Error during notificationTaskExecutor shutdown: {}", e.getMessage());
        }
    }

    /* ================= DTOs ================ */

    @Value @Builder
    public static class NotificationResult {
        NotificationType type;
        String recipient;
        boolean success;
        String error;
        Long logId;

        public static NotificationResult success(NotificationType type, String recipient, Long logId) {
            return NotificationResult.builder().type(type).recipient(recipient).success(true).logId(logId).build();
        }
        public static NotificationResult failure(NotificationType type, String recipient, String error) {
            return NotificationResult.builder().type(type).recipient(recipient).success(false).error(error).build();
        }
    }

    @Value
    public static class NotificationRequest {
        NotificationType type;
        String recipient;
        String subject;
        String message;
    }
}
