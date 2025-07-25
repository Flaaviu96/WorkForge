package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.*;
import dev.workforge.app.WorkForge.Mapper.AttachmentMapper;
import dev.workforge.app.WorkForge.Mapper.CommentMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.*;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CommentService commentService;
    private final FileServiceImpl fileService;
    private final WorkflowService workflowService;
    private final TaskMapper taskMapper;
    private final SecurityUserService securityUserService;
    private final UserService userService;

    public TaskServiceImpl(TaskRepository taskRepository, CommentService commentService, FileServiceImpl fileService, WorkflowService workflowService, TaskMapper taskMapper, SecurityUserService securityUserService, UserService userService) {
        this.taskRepository = taskRepository;
        this.commentService = commentService;
        this.fileService = fileService;
        this.workflowService = workflowService;
        this.taskMapper = taskMapper;
        this.securityUserService = securityUserService;
        this.userService = userService;
    }

    @Override
    public TaskDTO getTaskByIdAndProjectId(long taskId, long projectId) {
        Task task = fetchTaskAndCheck(taskId, projectId);
        List<PermissionType> permissionTypeList = securityUserService.getProjectPermissionForUser(projectId);
        return taskMapper.toDTO(task, permissionTypeList);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskWithoutCommentsAndAttachments(TaskDTO taskDTO, long projectId) {
        DTOValidator.validate(taskDTO);
        try {
            if (taskDTO.id() == GlobalEnum.INVALID_ID.getId() || projectId == GlobalEnum.INVALID_ID.getId()) {
                throw new TaskException(ErrorMessages.INVALID_ID + taskDTO.id(), HttpStatus.BAD_REQUEST);
            }
            Task task = fetchTaskAndCheck(taskDTO.id(), projectId);
            applyNonNullUpdates(task, taskDTO);
            return taskMapper.toDTO(task);
        } catch (OptimisticLockException ex) {
            throw new OptimisticLockException("Task was modified by another user. Please refresh and try again.", ex);
        }
    }

    @Override
    @Transactional
    public CommentDTO saveNewComment(CommentDTO commentDTO, long taskId, long projectId) {
        try {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
            Set<Comment> comments = task.getComments();

            Comment comment = new Comment();
            comment.setTask(task);
            comment.setProjectId(projectId);
            comment.setContent(commentDTO.content());
            comment.setAuthor(commentDTO.author());
            comments.add(comment);

            Comment savedComment = commentService.saveNewComment(comment);
            commentService.flushComment();
            if (savedComment == null) {
                throw new IllegalStateException("Failed to persist comment.");
            }

            return CommentMapper.INSTANCE.toCommentDTO(savedComment);
        } catch (OptimisticLockException e) {
            throw new OptimisticLockException("Another user has added a comment at the same time. Please try again.");
        }
    }

    @Override
    public CommentDTO updateComment(CommentDTO commentDTO, long taskId) {
        DTOValidator.validate(commentDTO);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<Comment> comments = task.getComments();

        if (comments == null || comments.isEmpty()) {
            throw new CommentException("No comments found for the task", HttpStatus.NOT_FOUND);
        }
        Comment comment = comments.stream()
                .filter(c -> c.getId() == commentDTO.id())
                .findFirst()
                .orElseThrow(() -> new CommentException("Comment not found", HttpStatus.NOT_FOUND));

        comment.setContent(commentDTO.content());
        taskRepository.save(task);

        return CommentMapper.INSTANCE.toCommentDTO(comment);
    }

    @Override
    public AttachmentDTO saveNewAttachment(MultipartFile file, long projectId, long taskId) throws IOException {
        Task task = taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        boolean duplicate = task.getAttachments().stream()
                .anyMatch(attachment -> attachment.getFileName().equals(file.getOriginalFilename()));
        if (duplicate) {
            throw new AttachmentException(ErrorMessages.ATTACHMENT_DUPLICATE, HttpStatus.BAD_REQUEST);
        }

        Path path = fileService.saveFile(file, task.getId(), task.getProject().getProjectName());
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setPath(path.toString());
        attachment.setFileName(file.getOriginalFilename());
        attachment.setProjectId(task.getProject().getId());
        attachment.setSize(file.getSize());

        task.getAttachments().add(attachment);
        Task updatedTask = taskRepository.saveAndFlush(task);

        Optional<Attachment> savedAttachment = updatedTask.getAttachments().stream()
                .filter(a -> a.getFileName().equals(file.getOriginalFilename()) && a.getPath().equals(path.toString()))
                .findFirst();
        return AttachmentMapper.INSTANCE.toDTO(savedAttachment.orElseThrow( () -> new RuntimeException("Error")));
    }

    @Override
    public Attachment downloadAttachment(long projectId, long taskId, long attachmentId) throws IOException {
        Task task = taskRepository.findTaskWithAttachments(taskId);
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = task.getAttachments().stream().filter(attachment -> attachment.getId() == attachmentId).findFirst();
        if (optionalAttachment.isPresent()) {
            return optionalAttachment.get();
        }
        throw new AttachmentException(ErrorMessages.ATTACHMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @Override
    public TaskPatchDTO updateTask(long projectId, long taskId, TaskPatchDTO taskPatchDTO) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (taskPatchDTO.taskTimeTrackingDTO() != null) {
            task.getTaskTimeTracking().setLoggedHours(taskPatchDTO.taskTimeTrackingDTO().loggedHours());
        }
        if (taskPatchDTO.taskMetadataDTO() != null) {
            task.getTaskMetadata().setDescription(taskPatchDTO.taskMetadataDTO().description());
        }
        if (taskPatchDTO.taskName() != null) {
            task.setTaskName(taskPatchDTO.taskName());
        }
        if (taskPatchDTO.toState() != null) {
            updateTaskState(projectId, taskId, taskPatchDTO.fromState(), taskPatchDTO.toState());
        }

        if (taskPatchDTO.userUUID() != null) {
            AppUser appUser = userService.getUserByUUID(UUID.fromString(taskPatchDTO.userUUID()));
            if (appUser == null) {
                throw new UserException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            task.getTaskMetadata().setAssignedTo(appUser.getUsername());
        }
        task = taskRepository.saveAndFlush(task);
        return taskMapper.toTaskPathDTO(task);
    }

    @Override
    public void deleteAttachment(long taskId, String attachment) {
        Task task = taskRepository.findTaskWithAttachments(taskId);
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Iterator<Attachment> attachmentIterator = task.getAttachments().iterator();
        while (attachmentIterator.hasNext()) {
            Attachment att = attachmentIterator.next();
            if (att.getFileName().equals(attachment)) {
                attachmentIterator.remove();
                att.setTask(null);
                break;
            }
        }
        taskRepository.save(task);
    }


    public void updateTaskState(long projectId, long taskId, String stateFromDTO, String stateToDTO) {
        DTOValidator.validate(stateFromDTO);
        DTOValidator.validate(stateToDTO);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
        boolean result = workflowService.isTransitionValid(projectId, stateFromDTO, stateToDTO);

        if (result) {
            State state = workflowService.getStateToByName(projectId, stateToDTO);
            task.setState(state);
            //workflowService.triggerStateTransition(workflowId,stateFromDTO, state);
            return;
        }
        throw new StateTransitionException(ErrorMessages.STATE_TRANSITION_NOT_VALID, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fetching the task from the database and check if is not null
     */
    private Task fetchTaskAndCheck(long taskId, long projectId) {
        Task task =  taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return task;
    }

    /**
     * Applies the non-null updates from the {@code TaskDTO} to the {@code Task} entity.
     * Only fields that are not {@code null} in the {@code TaskDTO} are updated.
     *
     * @param task the {@code Task} entity fetched from the database that needs to be updated
     * @param taskDTO the {@code TaskDTO} containing the new details to update the task with
     */
    private void applyNonNullUpdates(Task task, TaskDTO taskDTO) {
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (taskDTO.taskName() != null) {
            task.setTaskName(taskDTO.taskName());
        }

        if (taskDTO.taskMetadataDTO() != null) {
            TaskMetadataDTO metadataDTO = taskDTO.taskMetadataDTO();

            if (metadataDTO.description() != null) {
                task.getTaskMetadata().setDescription(metadataDTO.description());
            }

            if (metadataDTO.assignedTo() != null) {
                task.getTaskMetadata().setAssignedTo(metadataDTO.assignedTo());
            }
        }
    }
}