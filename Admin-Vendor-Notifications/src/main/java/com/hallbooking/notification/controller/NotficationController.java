package com.hallbooking.notification.controller;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.dto.OtpNotificationRequest;
import com.hallbooking.notification.dto.PushNotificationRequest;
import com.hallbooking.notification.dto.SmsRequest;
import com.hallbooking.notification.entity.NotificationLog;
import com.hallbooking.notification.repository.NotificationLogRepository;
import com.hallbooking.notification.service.EmailServiceImpl;
import com.hallbooking.notification.service.PushNotificationServiceImplementation;
import com.hallbooking.notification.service.SmsServiceImplementation;
import com.hallbooking.util.OtpGenerator;
import com.hallbooking.util.OtpRateLimiter;
import com.hallbooking.util.TemplateRenderer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotficationController {
    private final EmailServiceImpl emailService;
    private final SmsServiceImplementation smsService;
    private final TemplateRenderer templateRenderer;
    private final NotificationLogRepository logRepository;
    private final PushNotificationServiceImplementation pushNotificationService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @PostMapping("/test/otp/queue")
    public String pushOtpToQueue(@RequestBody OtpNotificationRequest request) {
        rabbitTemplate.convertAndSend("otp_queue", request);
        return "📤 OTP sent to RabbitMQ queue!";
    }




    // ✅ 1. Send Single Email
    @PostMapping("/email")
    public String sendEmail(@RequestBody @Valid EmailRequest request) {
        emailService.sendEmail(request);
        return "Email sent to " + request.getTo();
    }

    // ✅ 2. Send Single SMS
    @PostMapping("/sms")
    public String sendSms(@RequestBody @Valid SmsRequest request) {
        smsService.sendSms(request);
        return "SMS sent to " + request.getTo();
    }

    // ✅ 3. Bulk Email
    @PostMapping("/bulk-email")
    public String sendBulkEmail(@RequestBody List<EmailRequest> requests) {
        for (EmailRequest req : requests) {
            String otp = OtpGenerator.generateOtp();
            System.out.println("OTP for " + req.getTo() + ": " + otp);
            req.setBody("Your OTP is: " + otp); // Replace with your Thymeleaf template if needed
            req.setSubject("🔐 Your OTP Code");
            emailService.sendEmail(req);
        }
        return "Bulk Emails sent";
    }

    // ✅ 4. Bulk SMS
    @PostMapping("/bulk-sms")
    public String sendBulkSms(@RequestBody List<SmsRequest> requests) {
        for (SmsRequest req : requests) {
            smsService.sendSms(req);
        }
        return "Bulk SMS sent";
    }

    private final OtpRateLimiter otpRateLimiter;
    // ✅ 5. Send OTP via Email or SMS
    @PostMapping("/otp")
    public ResponseEntity<String> sendOtp(@RequestBody @Valid OtpNotificationRequest request) {
        String otp = OtpGenerator.generateOtp();
        request.setOtp(otp);
        System.out.println("otp:"+otp);
        try {
            rabbitTemplate.convertAndSend("otp_queue", request); // publish to queue
            return ResponseEntity.ok("✅ OTP sent via " + request.getType().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/push")
//    public String sendPush(@RequestBody Map<String, String> payload) {
    public String sendPush(@RequestBody PushNotificationRequest payload) {
        String token =payload.getToken();
        String title = payload.getTitle();
        String message = payload.getMessage();

        pushNotificationService.sendPush(token, title, message);
        return "✅ Push sent to token: " + token;

    }

    @PostMapping("/save-token")
    public ResponseEntity<String> saveFcmToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        System.out.println("📥 FCM Token received: " + token);
        return ResponseEntity.ok("Token saved");
    }

    @PostMapping("/debug-token")
    public String debugToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        System.out.println("🔍 Token received: '" + token + "'");
        System.out.println("Length: " + token.length());
        return "🔍 Token debug complete";
    }


    // ✅ 6. Booking confirmation (manual test)
    @PostMapping("/booking-confirmation")
    public String sendBookingConfirmation(@RequestParam String to,
                                          @RequestParam String name,
                                          @RequestParam String bookingId) {
        String html = templateRenderer.renderConfirmationTemplate(name, bookingId);
        EmailRequest email = new EmailRequest();
        email.setTo(to);
        email.setSubject("Booking Confirmation");
        email.setBody(html);
        emailService.sendEmail(email);
        return "Booking confirmation sent to " + to;
    }

    // ✅ 7. Fetch raw HTML template by type
    @GetMapping("/template/{type}")
    public String getTemplate(@PathVariable String type) {
        if (type.equalsIgnoreCase("otp")) {
            return templateRenderer.renderOtpTemplate("123456");
        } else if (type.equalsIgnoreCase("confirmation")) {
            return templateRenderer.renderConfirmationTemplate("Priyanka", "BOOKING123");
        } else {
            return "Template not found";
        }
    }

    // 1. Get All Logs
    @GetMapping("/logs")
    public List<NotificationLog> getAllLogs() {
        return logRepository.findAll();
    }

    // 2. Get Only Email Logs
    @GetMapping("/logs/email")
    public List<NotificationLog> getAllEmailLogs() {
        return logRepository.findByType(NotificationType.EMAIL);
    }

    // 3. Get Logs for a Specific Recipient
    @GetMapping("/logs/recipient/{to}")
    public List<NotificationLog> getByRecipient(@PathVariable String to) {
        return logRepository.findByRecipient(to);
    }

}
