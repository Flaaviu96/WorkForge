package dev.workforge.app.WorkForge.Service.Workflow.WorkflowImpl;

import dev.workforge.app.WorkForge.Model.StateTransition;
import dev.workforge.app.WorkForge.Repository.StateTransitionRepository;
import dev.workforge.app.WorkForge.Service.Workflow.StateTransitionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateTransitionServiceImpl implements StateTransitionService {

    private final StateTransitionRepository stateTransitionRepository;

    public StateTransitionServiceImpl(StateTransitionRepository stateTransitionRepository) {
        this.stateTransitionRepository = stateTransitionRepository;
    }

    @Override
    public List<StateTransition> getStatesTransitionsByWorkflowId(long workflowId) {
        return stateTransitionRepository.findAllStateTransitionByWorkflowId(workflowId);
    }
}
