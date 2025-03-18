package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Project;

import java.util.List;
import java.util.Optional;


public interface ProjectService {
     List<TaskDTO> getTasksWithCommentsByProjectId(long projectId);
     Optional<Project> getProjectByProjectId(Long projectId);
}
