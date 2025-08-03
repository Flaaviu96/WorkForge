package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import dev.workforge.app.WorkForge.Service.TaskService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SecurityUserService securityUserService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, SecurityUserService securityUserService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.securityUserService = securityUserService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/{taskId}")
    @PermissionCheck(permissionType = {PermissionType.READ})
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable long projectId,
            @PathVariable long taskId) {
        Task task = taskService.getTaskByIdAndProjectId(taskId, projectId);
        List<PermissionType> permissionTypeList = securityUserService.getProjectPermissionForUser(projectId);
        return ResponseEntity.ok(taskMapper.toDTO(task, permissionTypeList));
    }

    @PutMapping
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable long projectId,
            @RequestBody TaskDTO taskDTO
    ) {
        TaskDTO taskDTOUpdated = taskService.updateTaskWithoutCommentsAndAttachments(taskDTO, projectId);
        return ResponseEntity.ok(taskDTOUpdated);
    }

    @PatchMapping("{taskId}/metadata")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<TaskPatchDTO> updateTaskMetadata(
            @PathVariable long projectId,
            @PathVariable long taskId,
            @RequestBody TaskPatchDTO taskPatchDTO
    ) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, taskPatchDTO));
    }

    @PatchMapping("/{taskId}/comments")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<?> updateComment(
            @PathVariable long projectId,
            @PathVariable long taskId,
            @RequestBody CommentDTO commentDTO
    ) {
        return ResponseEntity.ok(taskService.updateComment(commentDTO, taskId));
    }

    @PostMapping("/{taskId}/comments")
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
            @PathVariable long attachmentId) throws IOException {
        Attachment attachment = taskService.downloadAttachment(projectId, taskId, attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(attachment.getPath())))
                .body(new InputStreamResource(new FileInputStream(new File(attachment.getPath()))));
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    @PermissionCheck(permissionType = {PermissionType.READ, PermissionType.WRITE})
    public ResponseEntity<String> deleteAttachment(
            @PathVariable long projectId,
            @PathVariable long taskId,
            @PathVariable long attachmentId) {
        taskService.deleteAttachment(taskId, attachmentId);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}
