package dev.workforge.app.WorkForge.ExceptionHandler;

import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException projectNotFoundException){
        return new ResponseEntity<>(projectNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
