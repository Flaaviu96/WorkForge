package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;

public interface TaskService {

    /**
     * Retrieves the task details for the specified task ID within the given project.
     *
     * @param taskId the ID of the task to retrieve
     * @param projectId the ID of the project to which the task belongs
     * @return the {@code TaskDTO} representing the task details
     */
    TaskDTO getTaskByIdAndProjectId(long taskId, long projectId);

    /**
     * Partially updates the details of the specified task (excluding comments and attachments).
     *
     * @param taskDTO the new details for the task
     * @param projectId the ID of the project to which the task belongs
     * @return the updated {@code TaskDTO} representing the task with the updated details
     */
    TaskDTO updateTaskWithoutCommentsAndAttachments(TaskDTO taskDTO, long projectId);


    /**
     * Adding the new comment to the collection of the task.
     *
     * @param commentDTO which represents the new comment
     * @param taskId the ID of the task where to store the new comment
     * @param projectId the ID of the project to which the task belongs
     */
    void saveNewComment(CommentDTO commentDTO, long taskId, long projectId);
}
