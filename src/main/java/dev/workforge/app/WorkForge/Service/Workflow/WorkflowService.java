package dev.workforge.app.WorkForge.Service.Workflow;

import dev.workforge.app.WorkForge.DTO.WorkflowDTO;
import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.Workflow;

/**
 * Service interface for managing workflows and state transitions.
 */
public interface WorkflowService {

    /**
     * Retrieves a workflow entity by its unique identifier.
     *
     * @param id the unique identifier of the workflow
     * @return the {@link Workflow} entity corresponding to the given id
     */
    Workflow getWorkflowById(long id);

    /**
     * Checks whether a transition from one state to another is valid
     * for the specified workflow.
     *
     * @param id        the workflow ID
     * @param stateFrom the current state's name
     * @param stateTo   the target state's name
     * @return true if the transition is valid; false otherwise
     */
    boolean isTransitionValid(long id, String stateFrom, String stateTo);

    /**
     * Retrieves a target state object by its name within a given workflow.
     *
     * @param workflowId the ID of the workflow
     * @param stateName  the name of the state to retrieve
     * @return the {@link State} object matching the given name
     */
    State getStateToByName(long workflowId, String stateName);

    /**
     * Triggers a state transition in a given workflow from the current state
     * to the specified target state.
     *
     * @param workflowId the ID of the workflow
     * @param stateFrom  the current state's name
     * @param stateTo    the target {@link State} object to transition to
     */
    void triggerStateTransition(long workflowId, String stateFrom, State stateTo);

    /**
     * Retrieves the workflow associated with a specific project by its ID.
     *
     * @param projectId the ID of the project
     * @return a {@link WorkflowDTO} representing the workflow data
     */
    WorkflowDTO getWorkflowByProjectId(int projectId);
}
