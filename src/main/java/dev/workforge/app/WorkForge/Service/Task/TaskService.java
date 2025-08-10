package dev.workforge.app.WorkForge.Service.Task;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.Task;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for managing tasks, including their comments, attachments, and metadata.
 */
public interface TaskService {

    /**
     * Retrieves the task entity by its ID within a given project.
     *
     * @param taskId    the ID of the task to retrieve
     * @param projectId the ID of the project to which the task belongs
     * @return the {@link Task} entity
     */
    Task getTaskByIdAndProjectId(long taskId, long projectId);

    /**
     * Partially updates the details of the specified task (excluding comments and attachments).
     *
     * @param taskDTO   the updated task data
     * @param projectId the ID of the project the task belongs to
     * @return the updated {@link TaskDTO}
     */
    TaskDTO updateTaskWithoutCommentsAndAttachments(TaskDTO taskDTO, long projectId);

    /**
     * Retrieves a paginated list of task summaries based on the provided filter and project ID.
     *
     * @param taskFilter the filter criteria
     * @param projectId  the project ID
     * @return a paginated result of {@link TaskSummaryDTO}
     */
    PageResultDTO<TaskSummaryDTO> getPaginatedTaskSummaries(TaskFilter taskFilter, long projectId);

    /**
     * Adds a new comment to a task.
     *
     * @param commentDTO the new comment data
     * @param taskId     the ID of the task to add the comment to
     * @param projectId  the ID of the project the task belongs to
     * @return the saved {@link CommentDTO}
     */
    CommentDTO saveNewComment(CommentDTO commentDTO, long taskId, long projectId);

    /**
     * Updates an existing comment in a task.
     *
     * @param commentDTO the updated comment data
     * @param taskId     the ID of the task containing the comment
     * @return the updated {@link CommentDTO}
     */
    CommentDTO updateComment(CommentDTO commentDTO, long taskId);

    /**
     * Adds a new attachment to a task.
     *
     * @param file      the file to attach
     * @param projectId the ID of the project the task belongs to
     * @param taskId    the ID of the task to attach the file to
     * @return the saved {@link AttachmentDTO}
     * @throws IOException if the file cannot be read or stored
     */
    AttachmentDTO saveNewAttachment(MultipartFile file, long projectId, long taskId) throws IOException;

    /**
     * Downloads an attachment from a task.
     *
     * @param projectId    the ID of the project
     * @param taskId       the ID of the task
     * @param attachmentId the ID of the attachment to retrieve
     * @return the {@link Attachment} stream
     * @throws IOException if the file cannot be accessed
     */
    Attachment downloadAttachment(long projectId, long taskId, long attachmentId) throws IOException;

    /**
     * Applies a patch update to a task.
     *
     * @param projectId     the ID of the project
     * @param taskId        the ID of the task
     * @param taskPatchDTO  the patch data to apply
     * @return the updated {@link TaskPatchRequestDTO}
     */
    TaskPatchResponseDTO updateTask(long projectId, long taskId, TaskPatchRequestDTO taskPatchDTO);

    /**
     * Deletes a specific attachment from a task.
     *
     * @param taskId       the ID of the task
     * @param attachmentId the ID of the attachment to delete
     */
    void deleteAttachment(long taskId, long attachmentId);
}
