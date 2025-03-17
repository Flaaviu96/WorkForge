package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Project;

import java.util.List;


public interface ProjectService {
     List<TaskDTO> getTasksWithCommentsByProjectId(long projectId);
     List<Project> getProjectsByProjectIds(List<Long>projectIds);
}
