package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Exceptions.WorkflowNotFoundException;
import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateTransition;
import dev.workforge.app.WorkForge.Model.Workflow;
import dev.workforge.app.WorkForge.Repository.WorkflowRepository;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found"));
    }

    @Override
    public void buildWorkflow(long id, State stateFrom, State stateTo) {
        Workflow workflow = workflowRepository.findWorkflowWithStateTransitions(id);
        List<State> states = workflowFactory.getStatesTo(id, stateFrom);
        if (states == null) {
            workflowFactory.addWorkflow(workflow);
            states = workflowFactory.getStatesTo(id, stateFrom);
        }
    }
}
