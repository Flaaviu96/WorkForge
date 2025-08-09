package dev.workforge.app.WorkForge.Service.Project;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectReadService {

    /**
     * Retrieves a project by its unique project ID.
     *
     * @param projectId the ID of the project to retrieve
     * @return an Optional containing the Project if found, or empty if not found
     */
    Optional<Project> getProjectByProjectId(Long projectId);

    /**
     * Retrieves all tasks for the specified project without including their comments.
     *
     * @param projectId the ID of the project whose tasks are to be fetched
     * @return a list of TaskDTO objects representing tasks without comments
     */
    List<TaskDTO> getTasksWithoutCommentsByProjectId(long projectId);

    /**
     * Retrieves all tasks for the specified project including metadata such as assignee and other summary information.
     *
     * @param projectId the ID of the project whose tasks are to be fetched
     * @return a list of TaskDTO objects with detailed task summaries
     */
    List<TaskDTO> getTasksWithSummaries(long projectId);

    /**
     * Retrieves the projects accessible to the current user, excluding the tasks within those projects.
     *
     * @param projectsIds a list of project IDs accessible by the user
     * @return a list of ProjectDTO objects representing projects without their tasks
     */
    List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds);

    /**
     * Retrieves the project ID corresponding to a given project key.
     *
     * @param projectKey the unique key associated with a project
     * @return the project ID as a String, or null/empty if not found
     */
    Long getProjectIdBasedOnProjectKey(String projectKey);

    /**
     * Retrieves a paginated list of tasks filtered by the provided TaskFilter criteria for a specific project.
     *
     * @param taskFilter the filter criteria to apply to tasks
     * @param projectId the ID of the project to which the tasks belong
     * @return a PageResultDTO containing the filtered task summaries and pagination info
     */
    PageResultDTO<TaskSummaryDTO> getTasksByFilter(TaskFilter taskFilter, long projectId);
}
