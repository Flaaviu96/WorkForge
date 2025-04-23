package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.DTO.TaskMetadataDTO;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.TaskNotFoundException;
import dev.workforge.app.WorkForge.Exceptions.TaskUpdateException;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.Comment;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.TaskService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
    public void saveNewComment(CommentDTO commentDTO, long taskId, long projectId) {
        try {
            Task task = fetchTaskAndCheck(taskId, projectId);
            Set<Comment> commentList = task.getComments();

            Comment comment = new Comment();
            comment.setTask(task);
            comment.setProjectId(projectId);
            comment.setContent(commentDTO.content());
            comment.setAuthor(commentDTO.author());
            commentList.add(comment);
        } catch (OptimisticLockException e) {
            throw new OptimisticLockException("Another user has added a comment at the same time. Please try again.");
        }
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
}
