package dev.workforge.app.WorkForge.DTO;

import java.util.HashSet;
import java.util.Set;

public class ProjectDTO implements DTO{
    private long id;

    private String projectName;

    private String projectDescription;

    private final Set<TaskDTO> taskDTOS = new HashSet<>();

    private WorkflowDTO workflowDTO;
}
