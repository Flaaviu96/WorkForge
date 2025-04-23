package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Exceptions.WorkflowNotFoundException;
import dev.workforge.app.WorkForge.Model.Workflow;
import dev.workforge.app.WorkForge.Repository.WorkflowRepository;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import org.springframework.stereotype.Service;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowServiceImpl(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Override
    public Workflow getWorkflowById(long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found"));
    }
}
