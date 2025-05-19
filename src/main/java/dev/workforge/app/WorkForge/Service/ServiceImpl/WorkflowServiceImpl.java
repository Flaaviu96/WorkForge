package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Exceptions.WorkflowException;
import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.Workflow;
import dev.workforge.app.WorkForge.Repository.WorkflowRepository;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import org.hibernate.jdbc.Work;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowFactory workflowFactory;

    public WorkflowServiceImpl(WorkflowRepository workflowRepository, WorkflowFactory workflowFactory) {
        this.workflowRepository = workflowRepository;
        this.workflowFactory = workflowFactory;
    }

    @Override
    public Workflow getWorkflowById(long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean isTransitionValid(long id, State stateFrom, State stateTo) {
        Workflow workflow = workflowRepository.findWorkflowWithStateTransitions(id);
        if (workflow == null) throw new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND);
        Map<State, AbstractTrigger> states = workflowFactory.getStatesTo(id, stateFrom);
        if (states == null) {
            workflowFactory.addWorkflow(workflow);
            states = workflowFactory.getStatesTo(id, stateFrom);
            return states.entrySet().stream()
                    .anyMatch(stateAbstractTriggerEntry -> stateAbstractTriggerEntry.getKey().getName().equals(stateTo.getName()));
        }
        return false;
    }

    @Override
    public State getStateToByName(long workflowId, String stateName) {
        return workflowFactory.getStateToByName(workflowId, stateName);
    }

    @Override
    public void triggerStateTransition(long workflowId, String stateFrom, State stateTo) {
        AbstractTrigger abstractTrigger = workflowFactory.getTrigger(workflowId, stateFrom, stateTo);
        if (abstractTrigger != null) {
            abstractTrigger.fire();
        }
    }
}
