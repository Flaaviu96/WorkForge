package dev.workforge.app.WorkForge.Projections;

public interface TaskProjection {
    String getTaskName();
    String getStateName();
    String getAssignedTo();
    double getRemainingHours();
    long getTaskId();

}
