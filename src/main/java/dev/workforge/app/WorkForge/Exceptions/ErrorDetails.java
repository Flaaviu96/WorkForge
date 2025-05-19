package dev.workforge.app.WorkForge.Exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDetails {
    private int status;
    private String message;
    private LocalDateTime localDateTime = LocalDateTime.now();
}
