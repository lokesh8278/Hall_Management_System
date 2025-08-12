package com.hallbooking.util;

import org.springframework.stereotype.Component;

@Component
public class MessageValidator {
    public boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{10,15}$");
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}