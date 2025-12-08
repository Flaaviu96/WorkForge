package dev.workforge.app.WorkForge.controllers;

import dev.workforge.app.WorkForge.dto.*;
import dev.workforge.app.WorkForge.mapper.TaskMapper;
import dev.workforge.app.WorkForge.model.Attachment;
import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.model.Task;
import dev.workforge.app.WorkForge.security.SecurityUserService;
import dev.workforge.app.WorkForge.service.task.TaskService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/tasks")
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
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ])")
    public ResponseEntity<TaskDTO> getTaskById(
            @RequestParam("projectId") long projectId,
            @PathVariable long taskId) {
        Task task = taskService.getTaskByIdAndProjectId(taskId, projectId);
        List<PermissionType> permissionTypeList = securityUserService.getContext().getProjectPermissionForUser(projectId);
        return ResponseEntity.ok(taskMapper.toDTO(task, permissionTypeList));
    }

    @PutMapping
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<TaskDTO> updateTask(
            @RequestParam long projectId,
            @RequestBody TaskDTO taskDTO
    ) {
        TaskDTO taskDTOUpdated = taskService.updateTaskWithoutCommentsAndAttachments(taskDTO, projectId);
        return ResponseEntity.ok(taskDTOUpdated);
    }

    @PatchMapping("{taskId}/metadata")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<TaskPatchResponseDTO> updateTaskMetadata(
            @RequestParam("projectId") long projectId,
            @PathVariable long taskId,
            @RequestBody TaskPatchRequestDTO taskPatchDTO
    ) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, taskPatchDTO));
    }

    @PatchMapping("/{taskId}/comments")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<?> updateComment(
            @RequestParam("projectId") long projectId,
            @PathVariable long taskId,
            @RequestBody CommentDTO commentDTO
    ) {
        return ResponseEntity.ok(taskService.updateComment(commentDTO, taskId));
    }

    @PostMapping("/{taskId}/comments")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<CommentDTO> saveNewComment(@RequestParam("projectId") long projectId, @PathVariable long taskId, @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(taskService.saveNewComment(commentDTO, taskId, projectId));
    }

    @PostMapping("/{taskId}/attachments")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<AttachmentDTO> saveNewAttachment(@RequestParam("projectId") long projectId, @PathVariable long taskId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(taskService.saveNewAttachment(multipartFile, projectId, taskId));
    }

    @GetMapping("/{taskId}/attachments/{attachmentId}")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ])")
    public ResponseEntity<Resource> downloadAttachment(
            @RequestParam("projectId") long projectId,
            @PathVariable long taskId,
            @PathVariable long attachmentId) throws IOException {
        Attachment attachment = taskService.downloadAttachment(projectId, taskId, attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(attachment.getPath())))
                .body(new InputStreamResource(new FileInputStream(new File(attachment.getPath()))));
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    @PreAuthorize("@permissionEvaluator.hasProjectPermission(#projectId, [T(PermissionType).READ, T(PermissionType).WRITE])")
    public ResponseEntity<String> deleteAttachment(
            @RequestParam("projectId") long projectId,
            @PathVariable long taskId,
            @PathVariable long attachmentId) {
        taskService.deleteAttachment(taskId, attachmentId);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}
