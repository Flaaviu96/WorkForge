package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.CreateProjectDTO;
import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectReadService;
import dev.workforge.app.WorkForge.Service.ProjectWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {

    private final ProjectWriteService projectService;
    private final ProjectReadService projectReadService;

    public ProjectController(ProjectWriteService projectService, ProjectReadService projectReadService) {
        this.projectService = projectService;
        this.projectReadService = projectReadService;
    }

    @PermissionCheck(permissionType = PermissionType.READ)
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksWithSummaries(@PathVariable long projectId) {
        return ResponseEntity.ok(projectReadService.getTasksWithSummaries(projectId));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithTasks() {
        // Using a dummy List because the aspect will be called before getting to the business logic of the ProjectService layer.
        return ResponseEntity.ok(projectReadService.getProjectsWithoutTasks(new ArrayList<Long>()));
    }

    @GetMapping("/projects/{projectKey}")
    public ResponseEntity<String> getProjectId(@PathVariable String projectKey) {
        return ResponseEntity.ok(projectReadService.getProjectIdBasedOnProjectKey(projectKey));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody CreateProjectDTO createProjectDTO) {
        ProjectDTO savedProjectDTO = projectService.saveNewProject(createProjectDTO);
        URI location = URI.create("/projects/" + savedProjectDTO.id());
        return ResponseEntity.created(location).body(savedProjectDTO);
    }

    @PermissionCheck(permissionType = PermissionType.PROJECT_ADMIN)
    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDTO> updateProjectPartially(
            @PathVariable Long projectId,
            @RequestBody ProjectDTO projectDTO
    ) {
        ProjectDTO updateProjectDTO = projectService.updateProjectPartially(projectId, projectDTO);
        return ResponseEntity.ok(updateProjectDTO);
    }

    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    @PostMapping("/projects/{projectId}/saveNewTask")
    public ResponseEntity<Void> saveNewTask(@PathVariable(name = "projectId") long projectId, @RequestBody TaskDTO taskDTO) {
        projectService.saveNewTaskIntoProject(projectId, taskDTO);
        return null;
    }
}
