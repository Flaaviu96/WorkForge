package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.TaskDTO;

import java.util.Set;

public interface ProjectService {
     Set<TaskDTO> getTasksWithCommentsByProjectId(long projectId);
}
