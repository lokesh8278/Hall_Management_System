package com.hallbooking.exception;

import com.hallbooking.userservice.exceptions.ResourceNotFoundException;
import com.hallbooking.userservice.exceptions.InvalidOTPException;
import com.hallbooking.userservice.exceptions.PasswordPolicyException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class OptimizedGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOTP(InvalidOTPException ex, WebRequest request) {
        log.warn("Invalid OTP attempt: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid OTP")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PasswordPolicyException.class)
    public ResponseEntity<ErrorResponse> handlePasswordPolicy(PasswordPolicyException ex, WebRequest request) {
        log.warn("Password policy violation: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Password Policy Violation")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("You don't have permission to access this resource")
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.warn("Bad credentials: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message("Invalid credentials provided")
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed at {}", request.getDescription(false));
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Validation Failed");
        errorResponse.setMessage("Request validation failed");
        errorResponse.setPath(request.getDescription(false));
        errorResponse.setValidationErrors(errors);
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.warn("Constraint violation: {} at {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation at {}: {}", request.getDescription(false), ex.getMessage());
        
        String message = "Data integrity violation occurred";
        if (ex.getMessage().contains("duplicate key")) {
            message = "A record with this information already exists";
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Integrity Violation")
                .message(message)
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, WebRequest request) {
        log.warn("File upload size exceeded at {}", request.getDescription(false));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error("File Too Large")
                .message("The uploaded file exceeds the maximum allowed size")
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.warn("Unsupported media type at {}: {}", request.getDescription(false), ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .error("Unsupported Media Type")
                .message("The media type is not supported for this request")
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error at {}: {}", request.getDescription(false), ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Error response DTOs
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        // Getters and setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public static class ErrorResponseBuilder {
            protected LocalDateTime timestamp;
            protected int status;
            protected String error;
            protected String message;
            protected String path;
            
            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
            public ErrorResponseBuilder status(int status) { this.status = status; return this; }
            public ErrorResponseBuilder error(String error) { this.error = error; return this; }
            public ErrorResponseBuilder message(String message) { this.message = message; return this; }
            public ErrorResponseBuilder path(String path) { this.path = path; return this; }
            
            public ErrorResponse build() {
                ErrorResponse response = new ErrorResponse();
                response.setTimestamp(timestamp);
                response.setStatus(status);
                response.setError(error);
                response.setMessage(message);
                response.setPath(path);
                return response;
            }
        }
    }

    public static class ValidationErrorResponse extends ErrorResponse {
        private Map<String, String> validationErrors;
        
        public ValidationErrorResponse() {
            super();
        }
        
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }
}