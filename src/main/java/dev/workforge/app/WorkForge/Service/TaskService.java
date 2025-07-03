package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.AttachmentDTO;
import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.StateDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    CommentDTO saveNewComment(CommentDTO commentDTO, long taskId, long projectId);


    /**
     * Updating the comment from the collection of the task
     *
     * @param commentDTO which represents the updated comment
     * @param taskId the ID of the task where to store the new comment
     */
    CommentDTO updateComment(CommentDTO commentDTO, long taskId);


    /**
     *  Adding a new attachment to the collection of the task
     *
     * @param file which represents the new attachment
     * @param projectId the ID of the project to which the task belongs
     * @param taskId the ID of the task where to store the new attachment
     * @return the persisted attachment
     * @throws IOException
     */
    AttachmentDTO saveNewAttachment(MultipartFile file, long projectId, long taskId) throws IOException;


    /**
     * Retrieve the attachment from the task with the specified ID
     *
     * @param projectId the ID of the project to which the task belongs
     * @param taskId the ID of the task from where we get the path for the attachment
     * @param attachmentName the name of the attachment entity
     * @return the stream of the attachment
     *  @throws IOException if the attachment file cannot be read
     */
    InputStreamResource downloadAttachment(long projectId, long taskId, String attachmentName) throws IOException;

    void updateTaskState(long workflowId, long taskId, StateDTO stateFromDTO, StateDTO stateToDTO);
}
