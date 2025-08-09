package dev.workforge.app.WorkForge.Service.Workflow.WorkflowImpl;

import dev.workforge.app.WorkForge.DTO.StateDTO;
import dev.workforge.app.WorkForge.DTO.WorkflowDTO;
import dev.workforge.app.WorkForge.Exceptions.WorkflowException;
import dev.workforge.app.WorkForge.Mapper.StateMapper;
import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.Workflow;
import dev.workforge.app.WorkForge.Repository.WorkflowRepository;
import dev.workforge.app.WorkForge.Service.Workflow.WorkflowService;
import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowFactory workflowFactory;
    private final StateMapper stateMapper;

    public WorkflowServiceImpl(WorkflowRepository workflowRepository, WorkflowFactory workflowFactory, StateMapper stateMapper) {
        this.workflowRepository = workflowRepository;
        this.workflowFactory = workflowFactory;
        this.stateMapper = stateMapper;
    }

    @Override
    public Workflow getWorkflowById(long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean isTransitionValid(long id, String stateFrom, String stateTo) {
        Map<State, AbstractTrigger> states = null;
        if (!workflowFactory.hasWorkflow(id)) {
            Workflow workflow = workflowRepository.findWorkflowByProjectId(id);
            if (workflow == null) throw new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND);
            workflowFactory.addWorkflow(workflow);
        }
        states = workflowFactory.getStatesTo(id, stateFrom);
        return states.entrySet().stream()
                .anyMatch(stateAbstractTriggerEntry -> stateAbstractTriggerEntry.getKey().getName().equals(stateTo));
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

    @Override
    public WorkflowDTO getWorkflowByProjectId(int projectId) {
        if (!workflowFactory.hasWorkflow(projectId)) {
            Workflow workflow = workflowRepository.findWorkflowByProjectId(projectId);
            workflowFactory.addWorkflow(workflow);
        }
        Map<State, List<State>> stateDTOListMap = workflowFactory.getWorkflowForSpecificProject(projectId);
        if (stateDTOListMap == null) {
            throw new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new WorkflowDTO(projectId, transformWorkflowToDTO(stateDTOListMap));
    }

    private Map<String, List<StateDTO>> transformWorkflowToDTO(Map<State, List<State>> stateDTOListMap) {
        return stateDTOListMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> entry.getValue().stream()
                                .map(stateMapper::toDTO)
                                .collect(Collectors.toList())
                ));
    }
}
