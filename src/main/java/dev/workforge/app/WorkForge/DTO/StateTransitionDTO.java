package dev.workforge.app.WorkForge.DTO;

public class StateTransitionDTO implements DTO{
    private long id;

    private StateDTO fromStateDTO;

    private StateDTO toStateDTO;

    private WorkflowDTO workflowDTO;
}
