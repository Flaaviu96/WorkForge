package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Project;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ProjectService {
     List<TaskDTO> getTasksWithoutCommentsByProjectId(long projectId);
     Optional<Project> getProjectByProjectId(Long projectId);
     List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds);
     void saveNewProject(ProjectDTO projectDTO);
     void saveNewTaskIntoProject(long projectId, TaskDTO taskDTO);
}
