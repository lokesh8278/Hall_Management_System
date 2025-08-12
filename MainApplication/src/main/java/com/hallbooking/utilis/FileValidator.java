package com.hallbooking.utilis;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    public boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        // Example: allow only PDF & JPEG/PNG
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/png")
        );
    }
}
