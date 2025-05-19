package dev.workforge.app.WorkForge.Exceptions;

import org.springframework.http.HttpStatus;

public class ProjectException extends AppException {
    public ProjectException(String message, HttpStatus status) {
        super(message, status);
    }
}
