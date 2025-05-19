package dev.workforge.app.WorkForge.Exceptions;

import org.springframework.http.HttpStatus;

public class AttachmentException extends AppException {
    public AttachmentException(String message, HttpStatus status) {
        super(message, status);
    }
}
