package com.hallbooking.userservice.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            // Try to get either getPassword() or getNewPassword()
            Method getPasswordMethod;
            try {
                getPasswordMethod = obj.getClass().getMethod("getNewPassword");
            } catch (NoSuchMethodException e) {
                getPasswordMethod = obj.getClass().getMethod("getPassword");
            }

            Method getConfirmPassword = obj.getClass().getMethod("getConfirmPassword");

            String password = (String) getPasswordMethod.invoke(obj);
            String confirmPassword = (String) getConfirmPassword.invoke(obj);

            return password != null && password.equals(confirmPassword);

        } catch (Exception e) {
            e.printStackTrace(); // log the issue
            return false;
        }
    }
}

