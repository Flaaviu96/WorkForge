package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.*;
import dev.workforge.app.WorkForge.Mapper.AttachmentMapper;
import dev.workforge.app.WorkForge.Mapper.CommentMapper;
import dev.workforge.app.WorkForge.Mapper.StateMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.Comment;
import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.CommentService;
import dev.workforge.app.WorkForge.Service.TaskService;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CommentService commentService;
    private final FileServiceImpl fileService;
    private final WorkflowService workflowService;

    public TaskServiceImpl(TaskRepository taskRepository, CommentService commentService, FileServiceImpl fileService, WorkflowService workflowService) {
        this.taskRepository = taskRepository;
        this.commentService = commentService;
        this.fileService = fileService;
        this.workflowService = workflowService;
    }

    @Override
    public TaskDTO getTaskByIdAndProjectId(long taskId, long projectId) {
        Task task = fetchTaskAndCheck(taskId, projectId);
        return TaskMapper.INSTANCE.toDTO(task);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskWithoutCommentsAndAttachments(TaskDTO taskDTO, long projectId) {
        DTOValidator.validate(taskDTO);
        try {
            if (taskDTO.id() == GlobalEnum.INVALID_ID.getId() || projectId == GlobalEnum.INVALID_ID.getId()) {
                throw new TaskUpdateException(ErrorMessages.INVALID_ID + taskDTO.id());
            }
            Task task = fetchTaskAndCheck(taskDTO.id(), projectId);
            applyNonNullUpdates(task, taskDTO);
            return TaskMapper.INSTANCE.toDTO(task);
        } catch (OptimisticLockException ex) {
            throw new OptimisticLockException("Task was modified by another user. Please refresh and try again.", ex);
        }
    }

    @Override
    @Transactional
    public CommentDTO saveNewComment(CommentDTO commentDTO, long taskId, long projectId) {
        DTOValidator.validate(commentDTO);
        try {
            Task task = fetchTaskAndCheck(taskId, projectId);
            Set<Comment> comments = task.getComments();

            Comment comment = new Comment();
            comment.setTask(task);
            comment.setProjectId(projectId);
            comment.setContent(commentDTO.content());
            comment.setAuthor(commentDTO.author());
            comments.add(comment);

            Comment savedComment = commentService.saveNewComment(comment);
            if (savedComment == null) {
                throw new IllegalStateException("Failed to persist comment.");
            }

            return CommentMapper.INSTANCE.toCommentDTO(savedComment);
        } catch (OptimisticLockException e) {
            throw new OptimisticLockException("Another user has added a comment at the same time. Please try again.");
        }
    }

    @Override
    public void updateComment(CommentDTO commentDTO, long taskId, long projectId) {
        DTOValidator.validate(commentDTO);
        Task task = fetchTaskAndCheck(taskId, projectId);
        Set<Comment> comments = task.getComments();

        if (comments != null && !comments.isEmpty()) {
            Optional<Comment> optionalComment = comments.stream().filter( streamComment -> streamComment.getId() == commentDTO.id()).findFirst();
            if (optionalComment.isPresent()) {
                Comment comment = optionalComment.get();
                comment.setContent(commentDTO.content());
                taskRepository.save(task);
            }
        }
    }

    @Override
    public AttachmentDTO saveNewAttachment(MultipartFile file, long projectId, long taskId) throws IOException {
        Task task = taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        Path path = fileService.saveFile(file, task.getId(), task.getProject().getProjectName());

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setPath(path.toString());
        attachment.setFileName(file.getName());
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
    public InputStreamResource downloadAttachment(long projectId, long taskId, String attachmentName) throws IOException {
        Task task = taskRepository.findTaskWithAttachments(taskId);
        if (task == null) {
            throw new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = task.getAttachments().stream().filter(attachment -> attachment.getFileName().equals(attachmentName)).findFirst();
        if (optionalAttachment.isPresent()) {
            return new InputStreamResource(getInputStream(optionalAttachment.get().getPath()));
        }

        throw new AttachmentNotFound(ErrorMessages.ATTACHMENT_NOT_FOUND);
    }

    @Override
    public void updateTaskState(long workflowId, long taskId, StateDTO stateFromDTO, StateDTO stateToDTO) {
        DTOValidator.validate(stateFromDTO);
        DTOValidator.validate(stateToDTO);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND));
        boolean result = workflowService.isTransitionValid(
                workflowId,
                StateMapper.INSTANCE.fromDTO(stateFromDTO),
                StateMapper.INSTANCE.fromDTO(stateToDTO)
        );

        if (result) {
            State state = workflowService.getStateByName(workflowId, stateToDTO.name());
            task.setState(state);
            taskRepository.save(task);
            return;
        }

        throw new StateTransitionNotValid(ErrorMessages.STATE_TRANSITION_NOT_VALID);
    }

    /**
     * Fetching the task from the database and check if is not null
     */
    private Task fetchTaskAndCheck(long taskId, long projectId) {
        Task task =  taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        if (task == null) {
            throw new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND);
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
            throw new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND);
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

    public InputStream getInputStream(String filePath) throws IOException {
        return new FileInputStream(new File(filePath));
    }
}