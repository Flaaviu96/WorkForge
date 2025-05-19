package dev.workforge.app.WorkForge.Exceptions;

import org.springframework.http.HttpStatus;

public class UserException extends AppException {
    public UserException(String message, HttpStatus status) {
        super(message, status);
    }
}
