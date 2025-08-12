package com.hallbooking.userservice.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
