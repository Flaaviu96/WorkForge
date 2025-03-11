package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PermissionCheck(permissionType = PermissionType.READ)
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectTasksWithComments(@PathVariable long projectId) {
        return ResponseEntity.ok(projectService.getTasksWithCommentsByProjectId(projectId));
    }


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

    }
}
