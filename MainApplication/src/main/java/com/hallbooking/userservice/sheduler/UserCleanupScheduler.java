package com.hallbooking.userservice.sheduler;


import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    @Transactional
    public void cleanupUsers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deactivateBefore = now.minusDays(60);
        LocalDateTime deleteBefore = now.minusDays(90);

        List<User> users = userRepository.findAll();
        int deactivated = 0;
        int deleted = 0;

        for (User user : users) {
            LocalDateTime lastLogin = user.getLastLoginAt();

            if (lastLogin == null) continue; // Skip if login never happened

            if (user.isActive() && lastLogin.isBefore(deactivateBefore)) {
                user.setActive(false);
                deactivated++;
            } else if (!user.isActive() && lastLogin.isBefore(deleteBefore)) {
                userRepository.delete(user);
                deleted++;
            }
        }

        System.out.printf("[Scheduler] ✅ Deactivated %d users, ❌ Deleted %d users%n", deactivated, deleted);
    }
}
