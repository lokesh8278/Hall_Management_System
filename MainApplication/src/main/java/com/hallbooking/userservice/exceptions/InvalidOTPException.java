package com.hallbooking.userservice.exceptions;

public class InvalidOTPException  extends RuntimeException{
    public InvalidOTPException(String message) {
        super(message);
    }
}
