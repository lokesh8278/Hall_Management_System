package com.hallbooking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
public void initFirebase() {
    try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase.json")) {
        if (serviceAccount == null) {
            throw new RuntimeException("❌ firebase.json not found in resources folder!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("springprojecthallmange")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase Initialized Successfully!");
        }
    } catch (Exception e) {
        System.out.println("❌ Firebase Initialization Failed: " + e.getMessage());
    }
}
}
