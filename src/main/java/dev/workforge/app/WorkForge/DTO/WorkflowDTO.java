package dev.workforge.app.WorkForge.DTO;



import java.util.Set;

public class WorkflowDTO implements DTO{
    private long id;

    private String workflowName;

    private String description;

    private Set<StateTransitionDTO> stateTransitionDTOS;

    private Set<ProjectDTO> projectDTOS;
}
