package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.AttachmentDTO;
import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.TaskService;
import io.github.bucket4j.Bucket;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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

    @PatchMapping
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable long projectId,
            @RequestBody TaskDTO taskDTO
    ) {
        TaskDTO taskDTOUpdated = taskService.updateTaskWithoutCommentsAndAttachments(taskDTO, projectId);
        return ResponseEntity.ok(taskDTOUpdated);
    }

//    @PatchMapping("/{taskId}/state")
//    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
//    public ResponseEntity<?> updateTaskState(TaskDTO taskDTO) {
//
//    }

    @PostMapping("/{taskId}")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<CommentDTO> saveNewComment(@PathVariable long projectId, @PathVariable long taskId, @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(taskService.saveNewComment(commentDTO, taskId, projectId));
    }

    @PostMapping("/{taskId}/attachments")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<AttachmentDTO> saveNewAttachment(@PathVariable long projectId, @PathVariable long taskId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(taskService.saveNewAttachment(multipartFile, projectId, taskId));
    }

    @GetMapping("/{taskId}/attachments/{attachmentId}")
    @PermissionCheck(permissionType = PermissionType.READ)
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable long projectId,
            @PathVariable long taskId,
            @PathVariable String attachmentId) throws IOException {

        return ResponseEntity.ok()
                .body(taskService.downloadAttachment(projectId, taskId, attachmentId));
    }
}
