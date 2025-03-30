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

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PermissionCheck(permissionType = PermissionType.READ)
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectWithTasks(@PathVariable long projectId) {
        return ResponseEntity.ok(projectService.getTasksWithoutCommentsByProjectId(projectId));
    }


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Using a dummy List because the aspect will be called before getting to the business logic of the ProjectService layer.
        return ResponseEntity.ok(projectService.getProjectsWithoutTasks(List.of(), PageRequest.of(page,size)));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody ProjectDTO projectDTO) {
        return null;
    }
}
