package dev.workforge.app.WorkForge.Service.Task.TaskImpl;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.*;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.TaskCriteriaRepository;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.ServiceImpl.DTOValidator;
import dev.workforge.app.WorkForge.Service.Task.AttachmentService;
import dev.workforge.app.WorkForge.Service.Task.CommentService;
import dev.workforge.app.WorkForge.Service.Task.TaskService;
import dev.workforge.app.WorkForge.Service.UserPermission.UserService;
import dev.workforge.app.WorkForge.Service.Workflow.WorkflowService;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final WorkflowService workflowService;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final TaskCriteriaRepository taskCriteriaRepository;

    public TaskServiceImpl(TaskRepository taskRepository, CommentService commentService, AttachmentService attachmentService, WorkflowService workflowService, TaskMapper taskMapper, UserService userService, TaskCriteriaRepository taskCriteriaRepository) {
        this.taskRepository = taskRepository;
        this.commentService = commentService;
        this.attachmentService = attachmentService;
        this.workflowService = workflowService;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.taskCriteriaRepository = taskCriteriaRepository;
    }

    @Override
    public Task getTaskByIdAndProjectId(long taskId, long projectId) {
        return fetchTaskAndCheck(taskId, projectId);
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
    public PageResultDTO<TaskSummaryDTO> getPaginatedTaskSummaries(TaskFilter taskFilter, long projectId) {
        if (taskFilter != null &&
                taskFilter.getTaskName() == null &&
                taskFilter.getCreatedDateFrom() == null &&
                taskFilter.getCreatedDateTo() == null &&
                taskFilter.getState() == null &&
                taskFilter.getAssignedTo() == null) {

            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return taskCriteriaRepository.findTasksByFilter(taskFilter, projectId);
    }

    @Override
    @Transactional
    public CommentDTO saveNewComment(CommentDTO commentDTO, long taskId, long projectId) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
            return commentService.saveNewComment(task, projectId, commentDTO);
    }

    @Override
    public CommentDTO updateComment(CommentDTO commentDTO, long taskId) {
        DTOValidator.validate(commentDTO);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND));
        return commentService.updateComment(task, commentDTO);
    }

    @Override
    public AttachmentDTO saveNewAttachment(MultipartFile file, long projectId, long taskId) throws IOException {
        Task task = taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        return attachmentService.saveNewAttachment(task, file, projectId);
    }

    @Override
    public Attachment downloadAttachment(long projectId, long taskId, long attachmentId) throws IOException {
        Task task = taskRepository.findTaskWithAttachments(taskId);
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return attachmentService.downloadAttachment(task,attachmentId);
    }

    @Override
    public void deleteAttachment(long taskId, long attachmentId) {
        Task task = taskRepository.findTaskWithAttachments(taskId);
        if (task == null) {
            throw new TaskException(ErrorMessages.TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        attachmentService.deleteAttachment(task, attachmentId);
    }

    @Override
    public TaskPatchResponseDTO updateTask(long projectId, long taskId, TaskPatchRequestDTO taskPatchDTO) {
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