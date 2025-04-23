package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.TaskService;
import io.github.bucket4j.Bucket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    @PermissionCheck(permissionType = {PermissionType.READ})
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable long projectId,
            @PathVariable long taskId) {
        return ResponseEntity.ok(taskService.getTaskByIdAndProjectId(taskId, projectId));
    }

    @PatchMapping()
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable long projectId,
            @RequestBody TaskDTO taskDTO
    ) {
        TaskDTO taskDTOUpdated = taskService.updateTaskWithoutCommentsAndAttachments(taskDTO, projectId);
        return ResponseEntity.ok(taskDTOUpdated);
    }

    @PostMapping("/{taskId}")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<TaskDTO> saveNewComment(@PathVariable long projectId, @PathVariable long taskId, @RequestBody CommentDTO commentDTO) {
        taskService.saveNewComment(commentDTO, taskId, projectId);
        return null;
    }
}
