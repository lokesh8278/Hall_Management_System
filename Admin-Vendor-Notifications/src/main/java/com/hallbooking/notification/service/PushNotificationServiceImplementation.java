package com.hallbooking.notification.service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationServiceImplementation {
    public void sendPush(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token.trim().replaceAll("^'+|'+$", ""))
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Push Sent: " + response);

        } catch (FirebaseMessagingException fme) {
            System.out.println("❌ FirebaseMessagingException Code: " + fme.getErrorCode());
            System.out.println("❌ FirebaseMessagingException: " + fme.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Failed to send push: " + e.getMessage());
            System.out.println("❌ General Error: " + e.getMessage());
        }
    }
}
