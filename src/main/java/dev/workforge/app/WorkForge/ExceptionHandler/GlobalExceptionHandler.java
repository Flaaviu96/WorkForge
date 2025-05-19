package dev.workforge.app.WorkForge.ExceptionHandler;

import dev.workforge.app.WorkForge.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDetails> handleProjectNotFoundException(AppException exception){
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(exception.getMessage());
        errorDetails.setStatus(exception.getStatus().value());
        return new ResponseEntity<>(errorDetails, exception.getStatus());
    }
}
