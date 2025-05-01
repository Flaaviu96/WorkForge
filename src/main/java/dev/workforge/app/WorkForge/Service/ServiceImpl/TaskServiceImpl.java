package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.AttachmentDTO;
import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.DTO.TaskMetadataDTO;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.AttachmentNotFound;
import dev.workforge.app.WorkForge.Exceptions.CommentInvalidException;
import dev.workforge.app.WorkForge.Exceptions.TaskNotFoundException;
import dev.workforge.app.WorkForge.Exceptions.TaskUpdateException;
import dev.workforge.app.WorkForge.Mapper.AttachmentMapper;
import dev.workforge.app.WorkForge.Mapper.CommentMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.Comment;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.CommentService;
import dev.workforge.app.WorkForge.Service.TaskService;
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

    public TaskServiceImpl(TaskRepository taskRepository, CommentService commentService, FileServiceImpl fileService) {
        this.taskRepository = taskRepository;
        this.commentService = commentService;
        this.fileService = fileService;
    }

    @Override
    public TaskDTO getTaskByIdAndProjectId(long taskId, long projectId) {
        Task task = fetchTaskAndCheck(taskId, projectId);
        return TaskMapper.INSTANCE.toDTO(task);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskWithoutCommentsAndAttachments(TaskDTO taskDTO, long projectId) {
        try {
            if (taskDTO.id() == GlobalEnum.INVALID_ID.getId() || projectId == GlobalEnum.INVALID_ID.getId()) {
                throw new TaskUpdateException("The ID of the task is not valid " + taskDTO.id());
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
        if (hasNullOrEmptyFields(commentDTO)) {
            throw new CommentInvalidException("The comment is invalid");
        }
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
        if (hasNullOrEmptyFields(commentDTO)) {
            throw new CommentInvalidException("The comment is invalid");
        }
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
            throw new TaskNotFoundException();
        }

        Optional<Attachment> optionalAttachment = task.getAttachments().stream().filter(attachment -> attachment.getFileName().equals(attachmentName)).findFirst();
        if (optionalAttachment.isPresent()) {
            return new InputStreamResource(getInputStream(optionalAttachment.get().getPath()));
        }

        throw new AttachmentNotFound("The attachment cannot be found");
    }

    /**
     * Fetching the task from the database and check if is not null
     */
    private Task fetchTaskAndCheck(long taskId, long projectId) {
        Task task =  taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        if (task == null) {
            throw new TaskNotFoundException();
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

    public  boolean hasNullOrEmptyFields(CommentDTO comment) {
        if (comment == null) return true;

        return comment.author() == null || comment.author().isEmpty()
                || comment.content() == null || comment.content().isEmpty();
    }

    public InputStream getInputStream(String filePath) throws IOException {
        return new FileInputStream(new File(filePath)); // Assuming your attachments are saved to disk
    }

}