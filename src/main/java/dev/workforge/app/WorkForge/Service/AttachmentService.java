package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.AttachmentDTO;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.Task;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for managing attachments related to tasks.
 */
public interface AttachmentService {

    /**
     * Saves a new attachment for the given task.
     *
     * @param task      the task to which the attachment will be added
     * @param file      the file to be attached
     * @param projectId the ID of the project to which the task belongs
     * @return the saved {@link AttachmentDTO} representing the new attachment
     * @throws IOException if the file cannot be read or stored
     */
    AttachmentDTO saveNewAttachment(Task task, MultipartFile file, long projectId) throws IOException;

    /**
     * Retrieves an attachment from the given task by its ID.
     *
     * @param task         the task containing the attachment
     * @param attachmentId the ID of the attachment to download
     * @return the {@link Attachment} entity
     */
    Attachment downloadAttachment(Task task, long attachmentId);

    /**
     * Deletes an attachment from the given task by its ID.
     *
     * @param task         the task containing the attachment
     * @param attachmentId the ID of the attachment to delete
     */
    void deleteAttachment(Task task, long attachmentId);
}
