package com.hallbooking.userservice.exceptions;

public class PasswordPolicyException extends RuntimeException{
    public PasswordPolicyException(String message) {
        super(message);
    }
}
