package dev.workforge.app.WorkForge.Projections;

import dev.workforge.app.WorkForge.Model.Task;

import java.util.Set;

public interface ProjectProjection {
    Long getProjectId();
    String getProjectName();
    Set<Task> getTasks();
}
