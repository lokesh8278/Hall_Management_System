package com.hallbooking.util;

public class PasswordValidator {
    public static boolean isStrong(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()].*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
