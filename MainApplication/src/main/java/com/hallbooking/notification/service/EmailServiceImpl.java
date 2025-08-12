package com.hallbooking.notification.service;


import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.dto.EmailRequest;
import com.hallbooking.notification.entity.NotificationLog;
import com.hallbooking.notification.repository.NotificationLogRepository;
import com.hallbooking.utilis.TemplateRenderer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {
    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;
    private final NotificationLogRepository logRepository;

    public void sendEmail(EmailRequest request) {
        boolean success = false;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true);

            mailSender.send(message);
            success = true;
            System.out.println("✅ Email sent to " + request.getTo());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + request.getTo());
        } finally {
            // Log to DB
            NotificationLog log = NotificationLog.builder()
                    .recipient(request.getTo())
                    .subject(request.getSubject())
                    .message(request.getPlainMessage())
                    .type(NotificationType.EMAIL)
                    .success(success)
                    .sentAt(LocalDateTime.now())
                    .build();
            logRepository.save(log);
        }
    }

//    public void sendOtp(String to, String otpCode) {
//        String body = templateRenderer.renderOtpTemplate(otpCode);
//        EmailRequest email = new EmailRequest();
//        email.setTo(to);
//        email.setSubject("Your OTP Code");
//        email.setBody(body);
//        sendEmail(email); // this already logs it
//    }

    public void sendOtp(String to, String otpCode) {
        Map<String, Object> code = new HashMap<>();
        code.put("otp", otpCode);

        String body = templateRenderer.renderEmailTemplate("otp", code);

        EmailRequest email = new EmailRequest();
        email.setTo(to);
        email.setSubject("Your OTP Code");
        email.setBody(body);

        sendEmail(email); // your existing email service
    }


}
