package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.DTO.TaskMetadataDTO;
import dev.workforge.app.WorkForge.Exceptions.TaskNotFoundException;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.Comment;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.TaskRepository;
import dev.workforge.app.WorkForge.Service.TaskService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.util.Set;

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
    public void updateTask(TaskDTO taskDTO, long projectId) {
        Task task = fetchTaskAndCheck(taskDTO.id(), projectId);
        applyNonNullUpdates(task, taskDTO);
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

    private Task fetchTaskAndCheck(long taskId, long projectId) {
        Task task =  taskRepository.findTaskByIdAndProjectId(taskId, projectId);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        return task;
    }

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
