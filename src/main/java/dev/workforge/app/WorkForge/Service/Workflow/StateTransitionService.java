package dev.workforge.app.WorkForge.Service.Workflow;

import dev.workforge.app.WorkForge.Model.StateTransition;

import java.util.List;

public interface StateTransitionService {

    List<StateTransition> getStatesTransitionsByWorkflowId(long workflowId);
}
