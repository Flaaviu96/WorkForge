package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.TaskDTO;

import java.util.List;


public interface ProjectService {
     List<TaskDTO> getTasksWithCommentsByProjectId(long projectId);
}
