package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Project;
import java.util.List;
import java.util.Optional;


public interface ProjectService {

     /**
      * Retrieves all tasks for the specified project without including their comments.
      *
      * @param projectId the ID of the project
      * @return a list of tasks without comments
      */
     ProjectDTO getTasksWithoutCommentsByProjectId(long projectId);

     /**
      * Retrieves the project with the specified ID.
      *
      * @param projectId the ID of the project
      * @return an Optional containing the project, if found; otherwise, an empty Optional
      */
     Optional<Project> getProjectByProjectId(Long projectId);

     /**
      * Retrieves the projects accessible to the current user, excluding their associated tasks.
      *
      * @param projectsIds the IDs of the projects accessible to the user
      * @return a list of projects without their tasks
      */
     List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds);

     /**
      * Persists a new project into the database.
      *
      * @param projectDTO the project data to be saved
      * @return the saved project including any generated fields (e.g., ID)
      */
     ProjectDTO saveNewProject(ProjectDTO projectDTO);

     /**
      * Persists a new task into the specified project
      *
      * @param projectId the ID of the project
      * @param taskDTO the task data to be saved
      * @return the saved task including any generated fields (e.g., ID)
      */
     TaskDTO saveNewTaskIntoProject(long projectId, TaskDTO taskDTO);

     /**
      * Partially updates the configuration or data of the project (e.g., tasks, project description).
      *
      * @param projectId the ID of the project to be updated
      * @param projectDTO the new project data to apply in the update
      * @return the updated project with the applied changes
      */
     ProjectDTO updateProjectPartially(long projectId, ProjectDTO projectDTO);
}
