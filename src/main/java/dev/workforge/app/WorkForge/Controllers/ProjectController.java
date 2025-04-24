package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PermissionCheck(permissionType = PermissionType.READ)
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectWithTasks(@PathVariable long projectId) {
        return ResponseEntity.ok(projectService.getTasksWithoutCommentsByProjectId(projectId));
    }


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithTasks() {
        // Using a dummy List because the aspect will be called before getting to the business logic of the ProjectService layer.
        return ResponseEntity.ok(projectService.getProjectsWithoutTasks(null));
    }


    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO savedProjectDTO = projectService.saveNewProject(projectDTO);
        URI location = URI.create("/projects/" + savedProjectDTO.id());
        return ResponseEntity.created(location).body(savedProjectDTO);
    }

    @PermissionCheck(permissionType = PermissionType.ADMIN)
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
