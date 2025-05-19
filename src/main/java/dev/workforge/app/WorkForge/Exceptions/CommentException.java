package dev.workforge.app.WorkForge.Exceptions;

import org.springframework.http.HttpStatus;

public class CommentException extends AppException {
    public CommentException(String message, HttpStatus status) {
        super(message, status);
    }
}
