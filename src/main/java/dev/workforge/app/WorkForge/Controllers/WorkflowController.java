package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.WorkflowDTO;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/workflows/{projectId}")
    public ResponseEntity<WorkflowDTO> getWorkflowByProjectId(@PathVariable int projectId) {
        return ResponseEntity.ok(workflowService.getWorkflowByProjectId(projectId));
    }
}
