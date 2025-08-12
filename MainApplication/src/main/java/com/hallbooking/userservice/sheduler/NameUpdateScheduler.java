package com.hallbooking.userservice.sheduler;


import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NameUpdateScheduler {
    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 30 2 * * ?") // Every day at 2:30 AM
    @Transactional
    public void capitalizeUserNames() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String name = user.getName();
            String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            user.setName(capitalized);
        }
        userRepository.saveAll(users);
        System.out.println("[Scheduler] Capitalized names for all users.");
    }
}
