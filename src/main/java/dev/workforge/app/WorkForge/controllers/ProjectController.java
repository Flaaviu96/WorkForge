package dev.workforge.app.WorkForge.controllers;

import dev.workforge.app.WorkForge.dto.*;
import dev.workforge.app.WorkForge.service.project.ProjectReadService;
import dev.workforge.app.WorkForge.service.project.ProjectWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //@PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ])")
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
    public ResponseEntity<Long> getProjectId(@PathVariable String projectKey) {
        return ResponseEntity.ok(projectReadService.getProjectIdBasedOnProjectKey(projectKey));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody CreateProjectDTO createProjectDTO) {
        ProjectDTO savedProjectDTO = projectService.saveNewProject(createProjectDTO);
        URI location = URI.create("/projects/" + savedProjectDTO.id());
        return ResponseEntity.created(location).body(savedProjectDTO);
    }

    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).ADMIN])")
    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDTO> updateProjectPartially(
            @PathVariable Long projectId,
            @RequestBody ProjectDTO projectDTO
    ) {
        ProjectDTO updateProjectDTO = projectService.updateProjectPartially(projectId, projectDTO);
        return ResponseEntity.ok(updateProjectDTO);
    }

    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    @PostMapping("/projects/{projectId}/saveNewTask")
    public void saveNewTask(@PathVariable(name = "projectId") long projectId, @RequestBody TaskDTO taskDTO) {
        projectService.saveNewTaskIntoProject(projectId, taskDTO);
    }

    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ])")
    @GetMapping("/projects/{projectId}/tasks/search")
    public ResponseEntity<PageResultDTO<TaskSummaryDTO>> getTasksUsingFilter(@PathVariable long projectId, @ModelAttribute TaskFilter taskFilter) {
        return ResponseEntity.ok(projectReadService.getTasksByFilter(taskFilter, projectId));
    }
}
