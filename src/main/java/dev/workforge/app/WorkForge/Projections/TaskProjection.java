package dev.workforge.app.WorkForge.Projections;

public interface TaskProjection {
    String getTaskName();
    String getStateName();
    String getAssignedTo();
    Double getRemainingHours();
    Long getTaskId();

}
