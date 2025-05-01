package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.Workflow;

public interface WorkflowService {

    /**
     * Fetching the workflow based on the given id
     * @param id the primary key of the workflow
     * @return the Workflow object
     */
    Workflow getWorkflowById(long id);

    void buildWorkflow(long id, State stateFrom, State stateTo);
}
