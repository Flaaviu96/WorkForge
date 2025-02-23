package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Set<TaskDTO>> getProjectTasksWithComments(@PathVariable long projectId) {
        return ResponseEntity.ok(projectService.getTasksWithCommentsByProjectId(projectId));
    }
}
