package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;

public interface TaskService {
    TaskDTO getTaskByIdAndProjectId(long taskId, long projectId);
    void updateTask(TaskDTO taskDTO, long projectId);
    void saveNewComment(CommentDTO commentDTO, long taskId, long projectId);
}
