package dev.workforge.app.WorkForge.Service.Task;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.Model.Task;

/**
 * Service interface for managing task comments.
 */
public interface CommentService {

    /**
     * Saves a new comment for the specified task.
     *
     * @param task      the task to which the comment will be added
     * @param projectId the ID of the project to which the task belongs
     * @param comment   the {@link CommentDTO} containing the new comment data
     * @return the saved {@link CommentDTO}
     */
    CommentDTO saveNewComment(Task task, long projectId, CommentDTO comment);

    /**
     * Updates an existing comment in the specified task.
     *
     * @param task        the task containing the comment to update
     * @param commentDTO  the {@link CommentDTO} with updated comment data
     * @return the updated {@link CommentDTO}
     */
    CommentDTO updateComment(Task task, CommentDTO commentDTO);
}
