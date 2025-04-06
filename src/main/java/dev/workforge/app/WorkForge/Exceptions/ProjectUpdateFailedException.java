package dev.workforge.app.WorkForge.Exceptions;

public class ProjectUpdateFailedException extends RuntimeException {
    public ProjectUpdateFailedException() {
        super("Project update failed");
    }

    public ProjectUpdateFailedException(String message) {
        super(message);
    }
}
