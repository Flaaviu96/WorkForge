package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.CreateProjectDTO;
import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;


public interface ProjectWriteService {

     /**
      * Persists a new project into the database.
      *
      * @param projectDTO the project data to be saved
      * @return the saved project including any generated fields (e.g., ID)
      */
     ProjectDTO saveNewProject(CreateProjectDTO createProjectDTO);

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
