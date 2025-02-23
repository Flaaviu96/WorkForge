package dev.workforge.app.WorkForge.Exceptions;

public class ProjectNotFoundException extends RuntimeException{

    public ProjectNotFoundException() {
        super("Project not found");
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
