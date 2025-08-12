package com.hallbooking.utilis;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static boolean isValid(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }
}