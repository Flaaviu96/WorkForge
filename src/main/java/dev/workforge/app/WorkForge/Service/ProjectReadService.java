package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectReadService {
    Optional<Project> getProjectByProjectId(Long projectId);

    /**
     * Retrieves all tasks for the specified project without including their comments.
     *
     * @param projectId the ID of the project
     * @return a list of tasks without comments
     */
    List<TaskDTO> getTasksWithoutCommentsByProjectId(long projectId);


    /**
     * Retrieve all tasks for the specified project with the metadata (e.g assginee,
     *
     * @param projectId
     * @return
     */
    List<TaskDTO> getTasksWithSummaries(long projectId);

    /**
     * Retrieves the projects accessible to the current user, excluding their associated tasks.
     *
     * @param projectsIds the IDs of the projects accessible to the user
     * @return a list of projects without their tasks
     */
    List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds);


    /**
     *
     *
     */
    String getProjectIdBasedOnProjectKey(String projectKey);

    PageResultDTO<TaskSummaryDTO> getTasksByFilter(TaskFilter taskFilter, long projectId);
}
