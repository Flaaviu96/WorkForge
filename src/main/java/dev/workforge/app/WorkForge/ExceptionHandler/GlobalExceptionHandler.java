package dev.workforge.app.WorkForge.ExceptionHandler;

import dev.workforge.app.WorkForge.Exceptions.PermissionNotFoundException;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException projectNotFoundException){
        return new ResponseEntity<>(projectNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationFailedException(AuthenticationException authenticationException) {
        return new ResponseEntity<>(authenticationException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<String> handlePermissionNotFoundException(PermissionNotFoundException permissionNotFoundException) {
        return new ResponseEntity<>(permissionNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
